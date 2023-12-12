package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.representations.TokenResponse;

public abstract class AbstractFlow extends AbstractRequest {

    private String scope;

    public AbstractFlow(OAuthRequest OAuthRequest, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(OAuthRequest, wellKnownSupplier);
        this.scope = scope;
    }

    public abstract TokenResponse execute() throws OpenIDException;

    public String getScope() {
        return scope != null ? scope : OAuthRequest.getScope();
    }

}
