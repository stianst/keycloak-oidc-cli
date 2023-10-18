package org.keycloak.oidc.representation.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;

import java.util.List;

public class JwtClaimsTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void stringAud() throws JsonProcessingException {
        String json = "{ \"aud\": \"single\" }";
        JwtClaims claims = objectMapper.readValue(json, JwtClaims.class);
        Assertions.assertArrayEquals(new String[] { "single"}, claims.getAud());
    }

    @Test
    public void arrayAud() throws JsonProcessingException {
        String json = "{ \"aud\": [ \"one\", \"two\" ] }";
        JwtClaims claims = objectMapper.readValue(json, JwtClaims.class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, claims.getAud());
    }

    @Test
    public void customClaim() throws JsonProcessingException {
        String json = "{ \"custom\": [ \"one\", \"two\" ], \"two\": \"two-value\" }";
        JwtClaims claims = objectMapper.readValue(json, JwtClaims.class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, (((List<?>) claims.getClaims().get("custom")).toArray(new String[] {})));
        Assertions.assertEquals("two-value", claims.getClaims().get("two"));
    }

}
