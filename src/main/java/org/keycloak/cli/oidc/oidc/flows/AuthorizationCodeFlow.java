package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.UriBuilder;
import org.keycloak.cli.oidc.http.server.BasicWebServer;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDResponseTypes;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.PKCE;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AuthorizationCodeFlow extends AbstractFlow {

    public AuthorizationCodeFlow(Context context, WellKnown wellKnown) {
        super(context, wellKnown);
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

        URI uri = UriBuilder.create(wellKnown.getAuthorizationEndpoint())
                .query(OpenIDParams.SCOPE, OpenIDScopes.OPENID)
                .query(OpenIDParams.RESPONSE_TYPE, OpenIDResponseTypes.CODE)
                .query(OpenIDParams.CLIENT_ID, context.getClientId())
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

            TokenResponse tokenResponse = clientRequest(wellKnown.getTokenEndpoint())
                    .contentType(MimeType.FORM)
                    .accept(MimeType.JSON)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.AUTHORIZATION_CODE)
                    .body(OpenIDParams.CODE, code)
                    .body(OpenIDParams.SCOPE, OpenIDScopes.OPENID)
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
