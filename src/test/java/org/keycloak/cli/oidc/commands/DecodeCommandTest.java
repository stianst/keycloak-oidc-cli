package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.jwt.Jwt;
import org.keycloak.oidc.mock.FakeJwt;

import java.io.IOException;

@QuarkusMainTest
public class DecodeCommandTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String testJwt;

    @BeforeEach
    public void before() {
        FakeJwt fakeJwt = new FakeJwt("http://localhost:8080", objectMapper);
        testJwt = fakeJwt.create(TokenType.ACCESS);
    }

    @Test
    public void testDecode(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("decode", "--token", testJwt);

        Jwt jwt = objectMapper.readValue(result.getOutput(), Jwt.class);
        Assertions.assertEquals("RS256", jwt.getHeader().getAlg());
        Assertions.assertEquals(TokenParser.parse(testJwt).getJwt().getHeader().getKid(), jwt.getHeader().getKid());
        Assertions.assertEquals("http://localhost:8080", jwt.getClaims().getIss());
        Assertions.assertEquals("aW52YWxpZA", jwt.getSignature());
    }

}
