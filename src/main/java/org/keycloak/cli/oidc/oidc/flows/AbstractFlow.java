package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

public abstract class AbstractFlow extends AbstractRequest {

    private String scope;

    public AbstractFlow(Context context, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(context, wellKnownSupplier);
        this.scope = scope;
    }

    public abstract TokenResponse execute() throws OpenIDException;

    public String getScope() {
        return scope != null ? scope : context.getScope();
    }

}
