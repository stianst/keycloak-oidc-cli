package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheContext;
import org.keycloak.cli.oidc.config.TokenCacheException;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;
import org.keycloak.oidc.mock.FakeJwt;
import org.keycloak.oidc.mock.OpenIDTestProviderExtension;
import org.keycloak.oidc.mock.RequestHandler;

@QuarkusMainTest
@ExtendWith({ OpenIDTestProviderExtension.class, ConfigHandlerExtension.class })
public class IntrospectCommandTest {

    private String accessToken;
    private String accessToken2;
    private String idToken;

    private ObjectMapper objectMapper = new ObjectMapper();
    private FakeJwt fakeJwt;

    @BeforeEach
    public void before(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws ConfigException, TokenCacheException {
        ConfigHandler configHandler = ConfigHandler.get();
        TokenCacheHandler tokenCacheHandler = TokenCacheHandler.get();

        fakeJwt = new FakeJwt(issuerUrl, objectMapper);

        accessToken = fakeJwt.create(TokenType.ACCESS);
        accessToken2 = fakeJwt.create(TokenType.ACCESS);
        idToken = fakeJwt.create(TokenType.ID);

        Context context1 = configHandler.getContext("context1");
        TokenCacheContext tokenCacheContext1 = tokenCacheHandler.getTokenCacheContext(context1);
        context1.setIssuer(issuerUrl);
        tokenCacheContext1.setAccessToken(fakeJwt.create(TokenType.ACCESS, true));

        Context context2 = configHandler.getCurrentContext();
        TokenCacheContext tokenCacheContext2 = tokenCacheHandler.getTokenCacheContext(context2);
        context2.setIssuer(issuerUrl);
        tokenCacheContext2.setAccessToken(accessToken);
        tokenCacheContext2.setIdToken(idToken);

        Context context3 = configHandler.getContext("context3");
        TokenCacheContext tokenCacheContext3 = tokenCacheHandler.getTokenCacheContext(context3);
        context3.setIssuer(issuerUrl);
        tokenCacheContext3.setAccessToken(accessToken2);

        configHandler.save();
        tokenCacheHandler.save();

        requestHandler.expectWellKnown();
        requestHandler.expectIntrospectionRequest();
    }

    @Test
    public void testIntrospectPassedToken(QuarkusMainLauncher launcher, @OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws ConfigException, JsonProcessingException {
        String token = fakeJwt.create(TokenType.ACCESS);

        LaunchResult result = launcher.launch("introspect", "--token=" + token);
        Assertions.assertEquals(0, result.exitCode());

        TokenIntrospectionResponse response = objectMapper.readValue(result.getOutput(), TokenIntrospectionResponse.class);
        Assertions.assertEquals(issuerUrl, response.getIss());
        Assertions.assertEquals(true, response.isActive());
        Assertions.assertEquals(TokenParser.parse(token).getJwt().getHeader().getKid(), response.getJti());

        Assertions.assertNotNull(requestHandler.pollRequest());

        HttpRequest introspectionRequest = requestHandler.pollRequest();
        Assertions.assertEquals(token, introspectionRequest.getBodyParams().get("token"));
    }

    @Test
    @Launch({ "introspect" })
    public void testIntrospectCachedToken(LaunchResult result, @OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws JsonProcessingException {
        Assertions.assertEquals(0, result.exitCode());

        TokenIntrospectionResponse response = objectMapper.readValue(result.getOutput(), TokenIntrospectionResponse.class);
        Assertions.assertEquals(issuerUrl, response.getIss());
        Assertions.assertEquals(true, response.isActive());
        Assertions.assertEquals(TokenParser.parse(accessToken).getJwt().getHeader().getKid(), response.getJti());

        Assertions.assertNotNull(requestHandler.pollRequest());

        HttpRequest introspectionRequest = requestHandler.pollRequest();
        Assertions.assertEquals(accessToken, introspectionRequest.getBodyParams().get("token"));
    }

    @Test
    @Launch({ "introspect", "--context=context3" })
    public void testIntrospectDifferentContext(LaunchResult result, @OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws JsonProcessingException {
        Assertions.assertEquals(0, result.exitCode());

        TokenIntrospectionResponse response = objectMapper.readValue(result.getOutput(), TokenIntrospectionResponse.class);
        Assertions.assertEquals(issuerUrl, response.getIss());
        Assertions.assertEquals(true, response.isActive());
        Assertions.assertEquals(TokenParser.parse(accessToken2).getJwt().getHeader().getKid(), response.getJti());

        Assertions.assertNotNull(requestHandler.pollRequest());

        HttpRequest introspectionRequest = requestHandler.pollRequest();
        Assertions.assertEquals(accessToken2, introspectionRequest.getBodyParams().get("token"));
    }

    @Test
    @Launch({ "introspect", "--context=context1" })
    public void testIntrospectExpired(LaunchResult result, @OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws JsonProcessingException {
        Assertions.assertEquals(0, result.exitCode());

        TokenIntrospectionResponse response = objectMapper.readValue(result.getOutput(), TokenIntrospectionResponse.class);
        Assertions.assertEquals(issuerUrl, response.getIss());
        Assertions.assertEquals(false, response.isActive());
    }

}
