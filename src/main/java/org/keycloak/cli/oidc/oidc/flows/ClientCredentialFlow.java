package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

public class ClientCredentialFlow extends AbstractFlow {

    public ClientCredentialFlow(Context configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    @Override
    public TokenResponse execute() throws TokenRequestFailure {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.CLIENT_CREDENTIAL)
                    .body(OpenIDParams.SCOPE, getScope())
                    .asObject(TokenResponse.class);
        } catch (Exception e) {
            throw new TokenRequestFailure(e);
        }
    }
}
