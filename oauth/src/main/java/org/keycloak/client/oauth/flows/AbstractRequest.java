package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.Http;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;

public class AbstractRequest {

    protected OpenIDRequest openIDRequest;
    protected OpenIDClient.WellKnownSupplier wellKnownSupplier;

    public AbstractRequest(OpenIDRequest openIDRequest, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        this.openIDRequest = openIDRequest;
        this.wellKnownSupplier = wellKnownSupplier;
    }

    protected Http clientRequest(String endpoint) {
        Http http = Http.create(endpoint).userAgent("kc-oidc/1.0");
        if (openIDRequest.getClientSecret() != null) {
            http.authorization(openIDRequest.getClientId(), openIDRequest.getClientSecret());
        } else {
            http.body("client_id", openIDRequest.getClientId());
        }
        return http;
    }

}
