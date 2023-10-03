package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.UserInfoResponse;

import java.io.IOException;

public class UserInfoRequest extends AbstractRequest {

    public UserInfoRequest(Context configuration, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
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
