package org.keycloak.client.oauth.exceptions;

import java.io.IOException;

public class DeviceAuthorizationRequestFailure extends OpenIDException {

    public DeviceAuthorizationRequestFailure(IOException e) {
        super("Device authorization request failed", e);
    }

}
