package org.keycloak.client.oauth.flows;

import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.representations.TokenResponse;

public abstract class AbstractFlow extends AbstractRequest {

    private String scope;

    public AbstractFlow(OpenIDRequest openIDRequest, String scope, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(openIDRequest, wellKnownSupplier);
        this.scope = scope;
    }

    public abstract TokenResponse execute() throws OpenIDException;

    public String getScope() {
        return scope != null ? scope : openIDRequest.getScope();
    }

}
