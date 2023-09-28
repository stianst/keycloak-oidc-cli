package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

public abstract class AbstractFlow extends AbstractRequest {

    public AbstractFlow(Context context, WellKnown wellKnown) {
        super(context, wellKnown);
    }

    public abstract TokenResponse execute() throws OpenIDException;

}
