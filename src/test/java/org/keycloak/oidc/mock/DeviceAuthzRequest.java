package org.keycloak.oidc.mock;

import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.http.HttpHeaders;
import org.keycloak.cli.oidc.http.HttpMethods;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.server.HttpRequest;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.DeviceAuthorizationResponse;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;
import org.testcontainers.shaded.com.github.dockerjava.core.exec.CreateSecretCmdExec;

import java.io.IOException;

public class DeviceAuthzRequest implements Request {

    private String issuer;

    public DeviceAuthzRequest(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public String getExpectedPath() {
        return "/authz/device";
    }

    @Override
    public void processRequest(HttpRequest httpRequest) throws IOException {
        Assertions.assertEquals(HttpMethods.POST, httpRequest.getMethod());
        Assertions.assertEquals(MimeType.FORM.toString(), httpRequest.getHeaderParams().get(HttpHeaders.CONTENT_TYPE));
        Assertions.assertEquals(MimeType.JSON.toString(), httpRequest.getHeaderParams().get(HttpHeaders.ACCEPT));

        DeviceAuthorizationResponse response = new DeviceAuthorizationResponse();
        response.setDeviceCode("thedevicecode");
        response.setInterval(1);
        response.setVerificationUri(issuer + "/device");
        response.setVerificationUriComplete(issuer + "/device?code=theusercode");
        response.setUserCode("theusercode");
        httpRequest.ok(Serializer.get().toBytes(response), MimeType.JSON);
    }
}
