package org.keycloak.cli.oidc.commands.converter;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.client.oauth.OpenIDRequest;

public class ContextConverter {

    public static OpenIDRequest toRequest(Context context) {
        OpenIDRequest request = new OpenIDRequest();
        request.setIssuer(context.getIssuer());
        request.setClientId(context.getClientId());
        request.setClientSecret(context.getClientSecret());
        request.setFlow(context.getFlow());
        request.setScope(context.getScope());
        request.setUsername(context.getUsername());
        request.setUserPassword(context.getUserPassword());
        request.setStoreTokens(context.isStoreTokens());
        return request;
    }

}
