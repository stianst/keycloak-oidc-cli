package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDGrantTypes;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.User;
import org.keycloak.client.oauth.exceptions.DeviceAuthorizationRequestFailure;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenRequestFailure;
import org.keycloak.client.oauth.representations.DeviceAuthorizationResponse;
import org.keycloak.client.oauth.representations.TokenResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeviceFlow extends AbstractFlow {

    private static final long DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);
    private static final long MAX_WAIT = TimeUnit.MINUTES.toMillis(5);

    public DeviceFlow(OpenIDRequest configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
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
