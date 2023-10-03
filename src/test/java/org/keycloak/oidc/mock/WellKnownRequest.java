package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpHeaders;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

import java.io.IOException;

public class WellKnownRequest implements Request {

    private String issuerUrl;

    public WellKnownRequest(String issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    @Override
    public String getExpectedPath() {
        return "/.well-known/openid-configuration";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.GET, httpRequest.getMethod());
        Assertions.assertEquals(MimeType.JSON.toString(), httpRequest.getHeaderParams().get(HttpHeaders.ACCEPT));

        WellKnown wellKnown = new WellKnown();
        wellKnown.setAuthorizationEndpoint(issuerUrl + "/authz");
        wellKnown.setTokenEndpoint(issuerUrl + "/tokens");
        wellKnown.setDeviceAuthorizationEndpoint(issuerUrl + "/authz/device");
        wellKnown.setIntrospectionEndpoint(issuerUrl + "/introspect");
        wellKnown.setUserinfoEndpoint(issuerUrl + "/userinfo");
        httpRequest.ok(Serializer.get().toBytes(wellKnown), MimeType.JSON);
    }
}
