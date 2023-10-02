package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.OpenIDParams;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthzRequest implements Request {

    @Override
    public String getExpectedPath() {
        return "/authz";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.GET, httpRequest.getMethod());

        String redirectUri = httpRequest.getQueryParams().get(OpenIDParams.REDIRECT_URI);
        String state = httpRequest.getQueryParams().get(OpenIDParams.STATE);
        String nonce = httpRequest.getQueryParams().get(OpenIDParams.NONCE);
        NonceHolder.nonce = nonce;

        String location = redirectUri + "?code=thecode&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
        httpRequest.found(location);
    }
}
