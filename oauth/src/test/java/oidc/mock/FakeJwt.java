package oidc.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.TokenType;
import org.keycloak.client.oauth.representations.jwt.JwtClaims;
import org.keycloak.client.oauth.representations.jwt.JwtHeader;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FakeJwt {

    private String iss;
    private ObjectMapper objectMapper;

    public FakeJwt(String iss, ObjectMapper objectMapper) {
        this.iss = iss;
        this.objectMapper = objectMapper;
    }

    public String create(TokenType tokenType) {
        return create(tokenType, false);
    }

    public String create(TokenType tokenType, boolean expired) {
        JwtHeader header = new JwtHeader();
        header.setKid(UUID.randomUUID().toString());
        header.setAlg("RS256");
        header.setTyp("JWT");

        long currentTime = (System.currentTimeMillis() / 1000);
        if (expired) {
            currentTime = currentTime - TimeUnit.MINUTES.toSeconds(5);
        }

        JwtClaims claims = new JwtClaims();
        claims.setIss(iss);
        claims.setIat(currentTime);
        claims.setExp(currentTime + TimeUnit.MINUTES.toSeconds(1));
        claims.getClaims().put("typ", tokenType.toString());

        if (tokenType.equals(TokenType.ID) && NonceHolder.nonce != null) {
            claims.getClaims().put(OpenIDParams.NONCE, NonceHolder.nonce);
            NonceHolder.nonce = null;
        }

        String signature = "invalid";

        try {
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String headerEncoded = encoder.encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            String claimEncoded = encoder.encodeToString(objectMapper.writeValueAsString(claims).getBytes(StandardCharsets.UTF_8));
            String signatureEncoded = encoder.encodeToString(signature.getBytes(StandardCharsets.UTF_8));
            return headerEncoded + "." + claimEncoded + "." + signatureEncoded;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String create(TokenType tokenType, JwtClaims claims) {
        JwtHeader header = new JwtHeader();
        header.setKid(UUID.randomUUID().toString());
        header.setAlg("RS256");
        header.setTyp("JWT");

        claims.getClaims().put("typ", tokenType.toString());

        String signature = "invalid";

        try {
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            String headerEncoded = encoder.encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            String claimEncoded = encoder.encodeToString(objectMapper.writeValueAsString(claims).getBytes(StandardCharsets.UTF_8));
            String signatureEncoded = encoder.encodeToString(signature.getBytes(StandardCharsets.UTF_8));
            return headerEncoded + "." + claimEncoded + "." + signatureEncoded;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
