package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.config.Config;
import org.keycloak.cli.oidc.config.Context;

public class ConfigCopy {

    public static Config copy(Config config, boolean brief) {
        Config copy = new Config();
        copy.setCurrent(config.getCurrent());
        for (Context c : config.getContexts()) {
            copy.getContexts().add(copy(c, brief));
        }
        return copy;
    }

    public static Context copy(Context context, boolean brief) {
        Context copy = new Context();
        copy.setName(context.getName());
        copy.setIssuer(context.getIssuer());
        copy.setFlow(context.getFlow());
        if (!brief) {
            copy.setClientId(context.getClientId());
            copy.setClientSecret(maskSecret(context.getClientSecret()));
            copy.setUsername(context.getUsername());
            copy.setUserPassword(maskSecret(context.getUserPassword()));
        }
        return copy;
    }

    private static String maskSecret(String value) {
        return value != null ? "********" : null;
    }

}
