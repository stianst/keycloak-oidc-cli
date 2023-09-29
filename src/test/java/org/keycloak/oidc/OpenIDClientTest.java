package org.keycloak.oidc;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.HttpHeaders;
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ExtendWith(OpenIDTestProviderExtension.class)
public class OpenIDClientTest {

    @Test
    public void testResourceOwner(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        Context context = createContext(issuerUrl, OpenIDFlow.RESOURCE_OWNER);
        OpenIDClient client = createClient(context, requestHandler);

        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();
        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest httpRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(httpRequest);
        Assertions.assertEquals("password", httpRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("theuser", httpRequest.getBodyParams().get("username"));
        Assertions.assertEquals("thepassword", httpRequest.getBodyParams().get("password"));
        Assertions.assertEquals("openid", httpRequest.getBodyParams().get("scope"));
    }

    @Test
    public void testClientCredential(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        Context context = createContext(issuerUrl, OpenIDFlow.CLIENT_CREDENTIAL);
        OpenIDClient client = createClient(context, requestHandler);

        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();
        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest httpRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(httpRequest);
        Assertions.assertEquals("client_credentials", httpRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("openid", httpRequest.getBodyParams().get("scope"));
        assertBasicAuthorization(httpRequest.getHeaderParams().get(HttpHeaders.AUTHORIZATION), "theclient", "thesecret");
    }

    private OpenIDClient createClient(Context context, RequestHandler requestHandler) throws OpenIDException {
        requestHandler.expectWellKnown();
        OpenIDClient client = new OpenIDClient(context);
        Assertions.assertNotNull(requestHandler.pollRequest());
        return client;
    }

    private Context createContext(String issuerUrl, OpenIDFlow flow) {
        Context context = new Context();
        context.setIssuer(issuerUrl);
        context.setFlow(flow);
        context.setClientId("theclient");
        context.setClientSecret("thesecret");
        context.setUsername("theuser");
        context.setUserPassword("thepassword");
        return context;
    }

    private Jwt parse(String token) {
        return TokenParser.parse(token).getJwt();
    }

    private void assertBasicAuthorization(String header, String username, String password) {
        Assertions.assertNotNull(header);
        String[] s = header.split(" ");
        Assertions.assertEquals("Basic", s[0]);
        String value = new String(Base64.getDecoder().decode(s[1]), StandardCharsets.UTF_8);
        Assertions.assertEquals(username + ":" + password, value);
    }

}
