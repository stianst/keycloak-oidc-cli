package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpHeaders;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.Jwt;

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
