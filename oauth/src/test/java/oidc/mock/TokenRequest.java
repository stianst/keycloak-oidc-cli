package oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.client.http.HttpHeaders;
import org.keycloak.client.http.HttpMethods;
import org.keycloak.client.http.MimeType;
import org.keycloak.client.http.server.HttpRequest;
import org.keycloak.client.oauth.TokenType;
import org.keycloak.client.oauth.representations.TokenResponse;

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
