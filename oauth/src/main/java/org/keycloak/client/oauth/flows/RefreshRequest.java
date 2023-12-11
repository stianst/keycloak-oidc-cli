package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDGrantTypes;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenRequestFailure;
import org.keycloak.client.oauth.representations.TokenResponse;

import java.io.IOException;

public class RefreshRequest extends AbstractRequest {

    private String scope;

    public RefreshRequest(OpenIDRequest configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, wellKnownSupplier);
        this.scope = scope;
    }

    public TokenResponse execute(String refreshToken) throws OpenIDException {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.REFRESH_TOKEN)
                    .body(OpenIDParams.REFRESH_TOKEN, refreshToken)
                    .body(OpenIDParams.SCOPE, scope != null ? scope : openIDRequest.getScope())
                    .asObject(TokenResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
