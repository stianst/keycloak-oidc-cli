package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
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
import org.keycloak.cli.oidc.kubectl.ExecCredentialRepresentation;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;
import org.keycloak.oidc.mock.FakeJwt;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class TokenCommandTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String expectedRefreshToken;
    private String expectedRefreshToken2;
    private String expectedAccessToken;
    private String expectedAccessToken2;
    private String expectedIdToken;
    private String expectedIdToken2;

    @BeforeEach
    public void before() throws ConfigException, TokenCacheException {
        ConfigHandler configHandler = ConfigHandler.get();
        TokenCacheHandler tokenCacheHandler = TokenCacheHandler.get();

        FakeJwt fakeJwt1 = new FakeJwt("http://localhost:8080/context1", objectMapper);
        this.expectedRefreshToken = fakeJwt1.create(TokenType.REFRESH);
        this.expectedAccessToken = fakeJwt1.create(TokenType.ACCESS);
        this.expectedIdToken = fakeJwt1.create(TokenType.ID);

        Context currentContext = configHandler.getCurrentContext();
        TokenCacheContext tokenCacheContext = tokenCacheHandler.getTokenCacheContext(currentContext);

        tokenCacheContext.setRefreshToken(expectedRefreshToken);
        tokenCacheContext.setAccessToken(expectedAccessToken);
        tokenCacheContext.setIdToken(expectedIdToken);

        FakeJwt fakeJwt2 = new FakeJwt("http://localhost:8080/context2", objectMapper);
        this.expectedRefreshToken2 = fakeJwt2.create(TokenType.REFRESH);
        this.expectedAccessToken2 = fakeJwt2.create(TokenType.ACCESS);
        this.expectedIdToken2 = fakeJwt2.create(TokenType.ID, true);

        Context context3 = configHandler.getContext("context3");
        TokenCacheContext tokenCacheContext3 = tokenCacheHandler.getTokenCacheContext(context3);
        tokenCacheContext3.setRefreshToken(expectedRefreshToken2);
        tokenCacheContext3.setAccessToken(expectedAccessToken2);
        tokenCacheContext3.setIdToken(expectedIdToken2);

        configHandler.save();
        tokenCacheHandler.save();
    }

    @Test
    @Launch({ "token" })
    public void testDefaultToken(LaunchResult result) {
        String token = result.getOutput();
        Assertions.assertEquals(expectedAccessToken, token);
    }

    @Test
    @Launch({ "token", "--context=context3" })
    public void testChangeContext(LaunchResult result) {
        String token = result.getOutput();
        Assertions.assertEquals(expectedAccessToken2, token);
    }

    @Test
    @Launch(value = { "token", "--context=context3", "--offline", "--type=id" }, exitCode = 1)
    public void testOfflineExpired(LaunchResult result) {
        Assertions.assertEquals("Token expired", result.getErrorOutput());
    }

    @Test
    @Launch(value = { "token", "--context=nosuch" }, exitCode = 1)
    public void testInvalidContext(LaunchResult result) {
        Assertions.assertEquals("Context 'nosuch' not found", result.getErrorOutput());
    }

    @Test
    @Launch({ "token", "--decode" })
    public void testDecode(LaunchResult result) throws JsonProcessingException {
        String token = result.getOutput();
        JwtClaims jwtClaims = objectMapper.readValue(token, JwtClaims.class);

        Assertions.assertEquals("http://localhost:8080/context1", jwtClaims.getIss());
        Assertions.assertNotNull(jwtClaims.getExp());
        Assertions.assertNotNull(jwtClaims.getIat());
        Assertions.assertEquals(TokenType.ACCESS.toString(), jwtClaims.getClaims().get("typ"));
    }

    @Test
    @Launch({ "token", "--type=id" })
    public void testIdToken(LaunchResult result) {
        String token = result.getOutput();
        Assertions.assertEquals(expectedIdToken, token);
    }

    @Test
    @Launch({ "token", "--kubectl" })
    public void testKubectl(LaunchResult result) throws JsonProcessingException {
        String token = result.getOutput();
        ExecCredentialRepresentation credentialRepresentation = objectMapper.readValue(token, ExecCredentialRepresentation.class);
        Assertions.assertEquals("client.authentication.k8s.io/v1", credentialRepresentation.getApiVersion());
        Assertions.assertEquals("ExecCredential", credentialRepresentation.getKind());
        Assertions.assertEquals(true, credentialRepresentation.getSpec().isInteractive());

        Assertions.assertEquals(expectedAccessToken, credentialRepresentation.getStatus().getToken());
        Assertions.assertNull(credentialRepresentation.getStatus().getExpirationTimestamp());
    }
    @Test
    @Launch({ "token", "--kubectl", "--type=id" })
    public void testKubectlWithIDToken(LaunchResult result) throws JsonProcessingException {
        String token = result.getOutput();
        ExecCredentialRepresentation credentialRepresentation = objectMapper.readValue(token, ExecCredentialRepresentation.class);
        Assertions.assertEquals("client.authentication.k8s.io/v1", credentialRepresentation.getApiVersion());
        Assertions.assertEquals("ExecCredential", credentialRepresentation.getKind());
        Assertions.assertEquals(true, credentialRepresentation.getSpec().isInteractive());

        Assertions.assertEquals(expectedIdToken, credentialRepresentation.getStatus().getToken());
        Assertions.assertNull(credentialRepresentation.getStatus().getExpirationTimestamp());
    }

}
