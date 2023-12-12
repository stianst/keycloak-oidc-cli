package oidc.representation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.kauth.oauth.representations.TokenIntrospectionResponse;

import java.util.List;

public class TokenIntrospectionResponseTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void stringAud() throws JsonProcessingException {
        String json = "{ \"aud\": \"single\" }";
        TokenIntrospectionResponse response = objectMapper.readValue(json, TokenIntrospectionResponse.class);
        Assertions.assertArrayEquals(new String[] { "single"}, response.getAud());
    }

    @Test
    public void arrayAud() throws JsonProcessingException {
        String json = "{ \"aud\": [ \"one\", \"two\" ] }";
        TokenIntrospectionResponse response = objectMapper.readValue(json, TokenIntrospectionResponse.class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, response.getAud());
    }

    @Test
    public void customClaim() throws JsonProcessingException {
        String json = "{ \"custom\": [ \"one\", \"two\" ], \"two\": \"two-value\" }";
        TokenIntrospectionResponse response = objectMapper.readValue(json, TokenIntrospectionResponse.class);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, (((List<?>) response.getClaims().get("custom")).toArray(new String[] {})));
        Assertions.assertEquals("two-value", response.getClaims().get("two"));
    }

}
