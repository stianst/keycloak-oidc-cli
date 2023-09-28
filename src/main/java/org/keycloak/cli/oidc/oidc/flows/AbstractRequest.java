package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.client.Http;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

public class AbstractRequest {

    protected Context context;
    protected WellKnown wellKnown;

    public AbstractRequest(Context context, WellKnown wellKnown) {
        this.context = context;
        this.wellKnown = wellKnown;
    }

    protected Http clientRequest(String endpoint) {
        Http http = Http.create(endpoint).userAgent("kc-oidc/1.0");
        if (context.getClientSecret() != null) {
            http.authorization(context.getClientId(), context.getClientSecret());
        } else {
            http.body("client_id", context.getClientId());
        }
        return http;
    }

}
