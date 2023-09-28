package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.config.Context;

public class AbstractConfigUpdaterCommandAbstract extends AbstractCommonOptionsCommand {

    public void update(Context context) {
        if (iss != null) {
            context.setIssuer(convertToNull(iss));
        }
        context.setFlow(flow);
        if (clientId != null) {
            context.setClientId(convertToNull(clientId));
        }
        if (clientSecret != null) {
            context.setClientSecret(convertToNull(clientSecret));
        }
        if (user != null) {
            context.setUsername(convertToNull(user));
        }
        if (password != null) {
            context.setUserPassword(convertToNull(password));
        }
        if (storeTokens != null) {
            String v = convertToNull(storeTokens);
            if (v != null) {
                context.setStoreTokens(Boolean.valueOf(v));
            } else {
                context.setStoreTokens(null);
            }
        }
        if (context.isStoreTokens() != null && !context.isStoreTokens()) {
            context.setRefreshToken(null);
            context.setAccessToken(null);
            context.setIdToken(null);
        }
    }

    private String convertToNull(String s) {
        return s != null && s.equals("null") ? null : s;
    }

}
