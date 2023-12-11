package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenRequestFailure;
import org.keycloak.client.oauth.representations.UserInfoResponse;

import java.io.IOException;

public class UserInfoRequest extends AbstractRequest {

    public UserInfoRequest(OpenIDRequest configuration, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, wellKnownSupplier);
    }

    public UserInfoResponse execute(String token) throws OpenIDException {
        try {
            return clientRequest(wellKnownSupplier.get().getUserinfoEndpoint())
                    .accept(MimeType.JSON)
                    .authorization(token)
                    .asObject(UserInfoResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
