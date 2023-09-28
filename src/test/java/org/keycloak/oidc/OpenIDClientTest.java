package org.keycloak.oidc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.Jwt;
import org.keycloak.oidc.mock.OpenIDTestProviderExtension;
import org.keycloak.oidc.mock.RequestHandler;

@ExtendWith(OpenIDTestProviderExtension.class)
public class OpenIDClientTest {

    @Test
    public void testResourceOwner(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        Context context = new Context();
        context.setIssuer(issuerUrl);
        context.setFlow(OpenIDFlow.RESOURCE_OWNER);
        context.setClientId("theclient");
        context.setClientSecret("thesecret");
        context.setUsername("theuser");
        context.setUserPassword("thepassword");

        requestHandler.expectWellKnown();

        OpenIDClient client = new OpenIDClient(context);

        requestHandler.pollRequest();

        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();
        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest httpRequest = requestHandler.pollRequest();
        Assertions.assertEquals("password", httpRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("theuser", httpRequest.getBodyParams().get("username"));
        Assertions.assertEquals("thepassword", httpRequest.getBodyParams().get("password"));
        Assertions.assertEquals("openid", httpRequest.getBodyParams().get("scope"));
    }

    private Jwt parse(String token) {
        return TokenParser.parse(token).getJwt();
    }

}
