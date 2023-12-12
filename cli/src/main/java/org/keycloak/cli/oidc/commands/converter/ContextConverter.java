package org.keycloak.cli.oidc.commands.converter;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.kauth.oauth.OAuthRequest;

public class ContextConverter {

    public static OAuthRequest toRequest(Context context) {
        OAuthRequest request = new OAuthRequest();
        request.setIssuer(context.getIssuer());
        request.setClientId(context.getClientId());
        request.setClientSecret(context.getClientSecret());
        request.setFlow(context.getFlow());
        request.setScope(context.getScope());
        request.setUsername(context.getUsername());
        request.setUserPassword(context.getUserPassword());
        return request;
    }

}
