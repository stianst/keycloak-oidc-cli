package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;

public abstract class AbstractFlow extends AbstractRequest {

    public AbstractFlow(Context context, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(context, wellKnownSupplier);
    }

    public abstract TokenResponse execute() throws OpenIDException;

}
