package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpHeaders;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

import java.io.IOException;

public class TokenRequest implements Request {

    private FakeJwt fakeJwt;

    public TokenRequest(FakeJwt fakeJwt) {

        this.fakeJwt = fakeJwt;
    }

    @Override
    public String getExpectedPath() {
        return "/tokens";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.POST, httpRequest.getMethod());
        Assertions.assertEquals(MimeType.FORM.toString(), httpRequest.getHeaderParams().get(HttpHeaders.CONTENT_TYPE));
        Assertions.assertEquals(MimeType.JSON.toString(), httpRequest.getHeaderParams().get(HttpHeaders.ACCEPT));

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(fakeJwt.create(TokenType.ACCESS));
        tokenResponse.setRefreshToken(fakeJwt.create(TokenType.REFRESH));
        tokenResponse.setIdToken(fakeJwt.create(TokenType.ID));
        httpRequest.ok(Serializer.get().toBytes(tokenResponse), MimeType.JSON);
    }
}
