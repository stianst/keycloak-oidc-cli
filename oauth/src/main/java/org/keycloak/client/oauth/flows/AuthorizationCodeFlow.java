package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.http.UriBuilder;
import org.keycloak.client.http.server.BasicWebServer;
import org.keycloak.client.http.server.HttpRequest;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDGrantTypes;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.OpenIDResponseTypes;
import org.keycloak.client.oauth.PKCE;
import org.keycloak.client.oauth.TokenParser;
import org.keycloak.client.oauth.User;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.representations.TokenResponse;
import org.keycloak.client.oauth.representations.jwt.JwtClaims;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AuthorizationCodeFlow extends AbstractFlow {

    public AuthorizationCodeFlow(OpenIDRequest openIDRequest, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(openIDRequest, scope, wellKnownSupplier);
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
                .query(OpenIDParams.SCOPE, getScope())
                .query(OpenIDParams.RESPONSE_TYPE, OpenIDResponseTypes.CODE)
                .query(OpenIDParams.CLIENT_ID, openIDRequest.getClientId())
                .query(OpenIDParams.REDIRECT_URI, redirectUri)
                .query(OpenIDParams.STATE, state)
                .query(OpenIDParams.NONCE, nonce)
                .query(OpenIDParams.CODE_CHALLENGE, pkce.getCodeChallenge())
                .query(OpenIDParams.CODE_CHALLENGE_METHOD, PKCE.S256)
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
            if (callback.getQueryParams().containsKey(OpenIDParams.ERROR)) {
                throw new OpenIDException("Authentication request failed: " + callback.getQueryParams().get(OpenIDParams.ERROR));
            }

            String code = callback.getQueryParams().get(OpenIDParams.CODE);
            String returnedState = callback.getQueryParams().get(OpenIDParams.STATE);

            if (!state.equals(returnedState)) {
                throw new OpenIDException("Invalid state parameter returned");
            }

            TokenResponse tokenResponse = clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .contentType(MimeType.FORM)
                    .accept(MimeType.JSON)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.AUTHORIZATION_CODE)
                    .body(OpenIDParams.CODE, code)
                    .body(OpenIDParams.SCOPE, getScope())
                    .body(OpenIDParams.REDIRECT_URI, redirectUri)
                    .body(OpenIDParams.CODE_VERIFIER, pkce.getCodeVerifier())
                    .asObject(TokenResponse.class);

            JwtClaims idToken = TokenParser.parse(tokenResponse.getIdToken()).getClaims();
            if (!nonce.equals(idToken.getClaims().get(OpenIDParams.NONCE))) {
                throw new OpenIDException("Invalid nonce parameter returned");
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
                byte[] body = BasicWebServer.class.getResource("favicon.ico").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.X_ICON);
            } else if (httpRequest.getPath().equals("/callback")) {
                byte[] body = BasicWebServer.class.getResource("callback.html").openStream().readAllBytes();
                httpRequest.ok(body, MimeType.HTML);
                return httpRequest;
            } else {
                httpRequest.badRequest();
            }
        }
    }

}
