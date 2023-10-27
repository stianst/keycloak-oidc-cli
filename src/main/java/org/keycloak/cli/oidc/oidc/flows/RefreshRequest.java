package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

import java.io.IOException;

public class RefreshRequest extends AbstractRequest {

    private String scope;

    public RefreshRequest(Context configuration, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
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
                    .body(OpenIDParams.SCOPE, scope != null ? scope : context.getScope())
                    .asObject(TokenResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
