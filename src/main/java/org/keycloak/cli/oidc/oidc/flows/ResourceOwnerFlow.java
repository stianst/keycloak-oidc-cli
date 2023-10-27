package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

public class ResourceOwnerFlow extends AbstractFlow {

    public ResourceOwnerFlow(Context configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
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
                    .body(OpenIDParams.USERNAME, context.getUsername())
                    .body(OpenIDParams.PASSWORD, context.getUserPassword())
                    .asObject(TokenResponse.class);
        } catch (Exception e) {
            throw new TokenRequestFailure(e);
        }
    }
}
