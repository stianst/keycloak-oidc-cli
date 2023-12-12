package oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.kauth.http.HttpHeaders;
import org.keycloak.kauth.http.HttpMethods;
import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.http.server.HttpRequest;
import org.keycloak.kauth.oauth.TokenParser;
import org.keycloak.kauth.oauth.representations.TokenIntrospectionResponse;
import org.keycloak.kauth.oauth.representations.jwt.Jwt;

import java.io.IOException;

public class TokenIntrospectionRequest implements Request {

    private String issuerUrl;

    public TokenIntrospectionRequest(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    @Override
    public String getExpectedPath() {
        return "/introspect";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.POST, httpRequest.getMethod());
        Assertions.assertEquals(MimeType.JSON.toString(), httpRequest.getHeaderParams().get(HttpHeaders.ACCEPT));

        Jwt jwt = TokenParser.parse(httpRequest.getBodyParams().get("token")).getJwt();

        TokenIntrospectionResponse response = new TokenIntrospectionResponse();
        response.setActive(jwt.getClaims().getExp() > (System.currentTimeMillis() / 1000));
        response.setJti(jwt.getHeader().getKid());
        response.setIss(jwt.getClaims().getIss());
        httpRequest.ok(Serializer.get().toBytes(response), MimeType.JSON);
    }
}
