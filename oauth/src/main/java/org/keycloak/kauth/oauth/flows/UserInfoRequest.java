package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.UserInfoResponse;

import java.io.IOException;

public class UserInfoRequest extends AbstractRequest {

    public UserInfoRequest(OAuthRequest configuration, OAuthClient.WellKnownSupplier wellKnownSupplier) {
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
