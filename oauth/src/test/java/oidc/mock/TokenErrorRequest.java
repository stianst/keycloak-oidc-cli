package oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.kauth.http.HttpHeaders;
import org.keycloak.kauth.http.HttpMethods;
import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.http.server.HttpRequest;
import org.keycloak.kauth.oauth.representations.TokenResponse;

import java.io.IOException;

public class TokenErrorRequest implements Request {

    private String error;

    public TokenErrorRequest(String error) {
        this.error = error;
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
        tokenResponse.setError(error);
        httpRequest.ok(Serializer.get().toBytes(tokenResponse), MimeType.JSON);
    }
}
