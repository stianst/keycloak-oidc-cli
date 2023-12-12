package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.http.UriBuilder;
import org.keycloak.kauth.http.server.BasicWebServer;
import org.keycloak.kauth.http.server.HttpRequest;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthGrantTypes;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.OAuthResponseTypes;
import org.keycloak.kauth.oauth.PKCE;
import org.keycloak.kauth.oauth.TokenParser;
import org.keycloak.kauth.oauth.User;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.representations.TokenResponse;
import org.keycloak.kauth.oauth.representations.jwt.JwtClaims;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AuthorizationCodeFlow extends AbstractFlow {

    public AuthorizationCodeFlow(OAuthRequest OAuthRequest, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(OAuthRequest, scope, wellKnownSupplier);
    }

    public TokenResponse execute() throws OpenIDException {
        if (!User.web().isDesktopSupported()) {
            throw new OpenIDException("Web browser not available");
        }

        BasicWebServer webServer;
        try {
            webServer = BasicWebServer.start();
        } catch (IOException e) {
            throw new OpenIDException("Failed to start callback server", e);
        }

        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();
        String redirectUri = "http://127.0.0.1:" + webServer.getPort() + "/callback";

        PKCE pkce = PKCE.create();

        URI uri = UriBuilder.create(wellKnownSupplier.get().getAuthorizationEndpoint())
                .query(OAuthParams.SCOPE, getScope())
                .query(OAuthParams.RESPONSE_TYPE, OAuthResponseTypes.CODE)
                .query(OAuthParams.CLIENT_ID, OAuthRequest.getClientId())
                .query(OAuthParams.REDIRECT_URI, redirectUri)
                .query(OAuthParams.STATE, state)
                .query(OAuthParams.NONCE, nonce)
                .query(OAuthParams.CODE_CHALLENGE, pkce.getCodeChallenge())
                .query(OAuthParams.CODE_CHALLENGE_METHOD, PKCE.S256)
                .toURI();

        try {
            User.web().browse(uri);
        } catch (IOException e) {
            throw new OpenIDException("Failed to open web browser", e);
        }

        HttpRequest callback;
        try {
            callback = waitForCallback(webServer);
            webServer.stop();
        } catch (IOException e) {
            throw new OpenIDException("Failed to process callback", e);
        }

        try {
            if (callback.getQueryParams().containsKey(OAuthParams.ERROR)) {
                throw new OpenIDException("Authentication request failed: " + callback.getQueryParams().get(OAuthParams.ERROR));
            }

            String code = callback.getQueryParams().get(OAuthParams.CODE);
            String returnedState = callback.getQueryParams().get(OAuthParams.STATE);

            if (!state.equals(returnedState)) {
                throw new OpenIDException("Invalid state parameter returned");
            }

            TokenResponse tokenResponse = clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .contentType(MimeType.FORM)
                    .accept(MimeType.JSON)
                    .body(OAuthParams.GRANT_TYPE, OAuthGrantTypes.AUTHORIZATION_CODE)
                    .body(OAuthParams.CODE, code)
                    .body(OAuthParams.SCOPE, getScope())
                    .body(OAuthParams.REDIRECT_URI, redirectUri)
                    .body(OAuthParams.CODE_VERIFIER, pkce.getCodeVerifier())
                    .asObject(TokenResponse.class);

            if (tokenResponse.getIdToken() != null) {
                JwtClaims idToken = TokenParser.parse(tokenResponse.getIdToken()).getClaims();
                if (!nonce.equals(idToken.getClaims().get(OAuthParams.NONCE))) {
                    throw new OpenIDException("Invalid nonce parameter returned");
                }
            }

            return tokenResponse;
        } catch (IOException e) {
            throw new OpenIDException("Failed to send authentication request");
        }
    }

    private HttpRequest waitForCallback(BasicWebServer webServer) throws IOException {
        while (true) {
            HttpRequest httpRequest = webServer.accept();
            if (httpRequest.getPath().equals("/favicon.ico")) {
                byte[] body = AuthorizationCodeFlow.class.getResource("favicon.ico").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.X_ICON);
            } else if (httpRequest.getPath().equals("/callback")) {
                byte[] body = AuthorizationCodeFlow.class.getResource("callback.html").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.HTML);
                return httpRequest;
            } else {
                httpRequest.badRequest();
            }
        }
    }

}
