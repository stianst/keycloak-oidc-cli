package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpHeaders;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.TokenParser;
import org.keycloak.cli.oidc.oidc.representations.UserInfoResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.Jwt;

import java.io.IOException;
import java.util.Map;

public class UserInfoRequest implements Request {

    private String issuerUrl;

    public UserInfoRequest(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    @Override
    public String getExpectedPath() {
        return "/userinfo";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.GET, httpRequest.getMethod());
        Assertions.assertEquals(MimeType.JSON.toString(), httpRequest.getHeaderParams().get(HttpHeaders.ACCEPT));

        Jwt jwt = TokenParser.parse(httpRequest.getHeaderParams().get("Authorization").split(" ")[1]).getJwt();
        Map<String, Object> claims = jwt.getClaims().getClaims();

        UserInfoResponse response = new UserInfoResponse();

        response.setName((String) claims.get("name"));
        response.setGivenName((String) claims.get("given_name"));
        response.setPreferredUsername((String) claims.get("preferred_username"));
        response.setFamilyName((String) claims.get("family_name"));
        response.setEmail((String) claims.get("email"));
        response.setSub(jwt.getClaims().getSub());
        response.setClaims(jwt.getClaims().getClaims());
        httpRequest.ok(Serializer.get().toBytes(response), MimeType.JSON);
    }
}
