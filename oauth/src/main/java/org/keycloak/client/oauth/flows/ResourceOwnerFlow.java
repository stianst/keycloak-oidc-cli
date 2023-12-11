package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDGrantTypes;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.exceptions.TokenRequestFailure;
import org.keycloak.client.oauth.representations.TokenResponse;

public class ResourceOwnerFlow extends AbstractFlow {

    public ResourceOwnerFlow(OpenIDRequest configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    @Override
    public TokenResponse execute() throws TokenRequestFailure {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.PASSWORD)
                    .body(OpenIDParams.SCOPE, getScope())
                    .body(OpenIDParams.USERNAME, openIDRequest.getUsername())
                    .body(OpenIDParams.PASSWORD, openIDRequest.getUserPassword())
                    .asObject(TokenResponse.class);
        } catch (Exception e) {
            throw new TokenRequestFailure(e);
        }
    }
}
