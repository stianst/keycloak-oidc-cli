package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthGrantTypes;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.User;
import org.keycloak.kauth.oauth.exceptions.DeviceAuthorizationRequestFailure;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.DeviceAuthorizationResponse;
import org.keycloak.kauth.oauth.representations.TokenResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DeviceFlow extends AbstractFlow {

    private static final long DEFAULT_POOL_INTERVAL = TimeUnit.SECONDS.toMillis(5);
    private static final long MAX_WAIT = TimeUnit.MINUTES.toMillis(5);

    public DeviceFlow(OAuthRequest configuration, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    public TokenResponse execute() throws OpenIDException {
        DeviceAuthorizationResponse deviceAuthorizationResponse;
        try {
            deviceAuthorizationResponse = clientRequest(wellKnownSupplier.get().getDeviceAuthorizationEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OAuthParams.SCOPE, getScope())
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
                        .body(OAuthParams.GRANT_TYPE, OAuthGrantTypes.DEVICE_CODE)
                        .body(OAuthParams.DEVICE_CODE, deviceAuthorizationResponse.getDeviceCode())
                        .body(OAuthParams.SCOPE, getScope())
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
