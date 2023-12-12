package org.keycloak.kauth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.kauth.oauth.representations.jwt.Jwt;
import org.keycloak.kauth.oauth.representations.jwt.JwtClaims;
import org.keycloak.kauth.oauth.representations.jwt.JwtHeader;

import java.io.IOException;
import java.util.Base64;

public class TokenParser {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Jwt jwt;

    private TokenParser(String token) {
        String[] split = token.split("\\.");
        jwt = new Jwt();
        try {
            jwt.setHeader(new ObjectMapper().readValue(Base64.getDecoder().decode(split[0]), JwtHeader.class));
            jwt.setClaims(new ObjectMapper().readValue(Base64.getDecoder().decode(split[1]), JwtClaims.class));
            jwt.setSignature(split[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TokenParser parse(String token) {
        return new TokenParser(token);
    }

    public Jwt getJwt() {
        return jwt;
    }

    public JwtClaims getClaims() {
        return jwt.getClaims();
    }

    public String getClaimsDecoded() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwt.getClaims());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getJwtDecoded() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
