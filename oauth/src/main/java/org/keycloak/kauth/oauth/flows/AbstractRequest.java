package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.Http;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;

public class AbstractRequest {

    protected OAuthRequest OAuthRequest;
    protected OAuthClient.WellKnownSupplier wellKnownSupplier;

    public AbstractRequest(OAuthRequest OAuthRequest, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        this.OAuthRequest = OAuthRequest;
        this.wellKnownSupplier = wellKnownSupplier;
    }

    protected Http clientRequest(String endpoint) {
        Http http = Http.create(endpoint).userAgent("kc-oidc/1.0");
        if (OAuthRequest.getClientSecret() != null) {
            http.authorization(OAuthRequest.getClientId(), OAuthRequest.getClientSecret());
        } else {
            http.body("client_id", OAuthRequest.getClientId());
        }
        return http;
    }

}
