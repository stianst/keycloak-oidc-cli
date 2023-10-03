package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.UserInfoResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;
import org.keycloak.oidc.mock.FakeJwt;
import org.keycloak.oidc.mock.OpenIDTestProviderExtension;
import org.keycloak.oidc.mock.RequestHandler;

@QuarkusMainTest
@ExtendWith({ OpenIDTestProviderExtension.class, ConfigHandlerExtension.class })
public class UserInfoCommandTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String token;
    private FakeJwt fakeJwt;

    @BeforeEach
    public void before(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @ConfigHandlerExtension.Config ConfigHandler configHandler, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws ConfigException {
        fakeJwt = new FakeJwt(issuerUrl, new ObjectMapper());

        JwtClaims claims = new JwtClaims();
        claims.setSub("thesub");
        claims.getClaims().put("name", "thename");
        claims.getClaims().put("preferred_username", "thepreferredusername");
        claims.getClaims().put("random", "therandom");

        token = fakeJwt.create(TokenType.ACCESS, claims);

        Context context = configHandler.getCurrentContext();
        context.setIssuer(issuerUrl);
        context.setAccessToken(token);

        configHandler.save();

        requestHandler.expectWellKnown();
        requestHandler.expectUserInfoRequest();
    }

    @Test
    public void testUserInfoCachedToken(QuarkusMainLauncher launcher) throws JsonProcessingException {
        LaunchResult result = launcher.launch("userinfo");

        Assertions.assertEquals(0, result.exitCode());

        UserInfoResponse userInfoResponse = objectMapper.readValue(result.getOutput(), UserInfoResponse.class);
        Assertions.assertEquals("thesub", userInfoResponse.getSub());
        Assertions.assertEquals("thename", userInfoResponse.getName());
        Assertions.assertEquals("thepreferredusername", userInfoResponse.getPreferredUsername());
        Assertions.assertEquals("therandom", userInfoResponse.getClaims().get("random"));
    }

    @Test
    public void testUserInfoToken(QuarkusMainLauncher launcher) throws JsonProcessingException {
        JwtClaims claims = new JwtClaims();
        claims.setSub("thesub2");
        claims.getClaims().put("name", "thename2");
        claims.getClaims().put("preferred_username", "thepreferredusername2");
        claims.getClaims().put("random", "therandom2");

        String token = fakeJwt.create(TokenType.ACCESS, claims);

        LaunchResult result = launcher.launch("userinfo", "--token=" + token);
        Assertions.assertEquals(0, result.exitCode());

        UserInfoResponse userInfoResponse = objectMapper.readValue(result.getOutput(), UserInfoResponse.class);
        Assertions.assertEquals("thesub2", userInfoResponse.getSub());
        Assertions.assertEquals("thename2", userInfoResponse.getName());
        Assertions.assertEquals("thepreferredusername2", userInfoResponse.getPreferredUsername());
        Assertions.assertEquals("therandom2", userInfoResponse.getClaims().get("random"));
    }

}
