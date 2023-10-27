package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.exceptions.DeviceAuthorizationRequestFailure;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.DeviceAuthorizationResponse;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeviceFlow extends AbstractFlow {

    private static final long DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);
    private static final long MAX_WAIT = TimeUnit.MINUTES.toMillis(5);

    public DeviceFlow(Context configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    public TokenResponse execute() throws OpenIDException {
        DeviceAuthorizationResponse deviceAuthorizationResponse;
        try {
            deviceAuthorizationResponse = clientRequest(wellKnownSupplier.get().getDeviceAuthorizationEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.SCOPE, getScope())
                    .asObject(DeviceAuthorizationResponse.class);
        } catch (IOException e) {
            throw new DeviceAuthorizationRequestFailure(e);
        }

        if (deviceAuthorizationResponse.getVerificationUriComplete() != null) {
            User.cli().print("Open the following URL to complete login:",
                    deviceAuthorizationResponse.getVerificationUriComplete());
        } else {
            User.cli().print("Open the following URL to complete login:",
                    deviceAuthorizationResponse.getVerificationUri(),
                    "",
                    "Enter the code:",
                    deviceAuthorizationResponse.getUserCode());
        }

        long interval = deviceAuthorizationResponse.getInterval() != null ? TimeUnit.SECONDS.toMillis(deviceAuthorizationResponse.getInterval()) : DEFAULT_POOL_INTERVAL;
        long stop = System.currentTimeMillis() + MAX_WAIT;

        while (System.currentTimeMillis() < stop) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                break;
            }

            TokenResponse tokenResponse;
            try {
                tokenResponse = clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                        .contentType(MimeType.FORM)
                        .accept(MimeType.JSON)
                        .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.DEVICE_CODE)
                        .body(OpenIDParams.DEVICE_CODE, deviceAuthorizationResponse.getDeviceCode())
                        .body(OpenIDParams.SCOPE, getScope())
                        .asObject(TokenResponse.class);
            } catch (IOException e) {
                throw new TokenRequestFailure(e);
            }

            if (tokenResponse.getError() == null || !tokenResponse.getError().equals("authorization_pending")) {
                return tokenResponse;
            }
        }

        throw new RuntimeException("Device authorization request timed out");
    }
}
