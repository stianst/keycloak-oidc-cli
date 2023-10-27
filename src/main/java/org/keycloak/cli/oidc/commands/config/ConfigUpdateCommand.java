package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.commands.converter.OpenIDFlowConverter;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Updates values for a configuration context")
public class ConfigUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name", required = true)
    String context;

    @CommandLine.Option(names = {"--issuer"}, description = "Issuer URL")
    String iss;

    @CommandLine.Option(names = {"--flow"}, description = "Flow (authorization-code, client-credential, device, resource-owner)", converter = OpenIDFlowConverter.class)
    OpenIDFlow flow;

    @CommandLine.Option(names = {"--client-id"}, description = "Client ID")
    String clientId;

    @CommandLine.Option(names = {"--client-secret"}, description = "Client secret")
    String clientSecret;

    @CommandLine.Option(names = {"--scope"}, description = "Scope")
    String scope;

    @CommandLine.Option(names = {"--user"}, description = "User name for resource-owner flow")
    String user;

    @CommandLine.Option(names = {"--user-password"}, description = "User password for resource-owner flow")
    String password;

    @CommandLine.Option(names = {"--store-tokens"}, description = "Store tokens")
    Boolean storeTokens;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            TokenCacheHandler tokenCacheHandler = TokenCacheHandler.get();
            Context context = ConfigHandler.get().getContext(this.context);

            if (iss != null) {
                context.setIssuer(checkNotNull(iss));
            }
            if (flow != null) {
                context.setFlow(flow);
            }
            if (storeTokens != null) {
                context.setStoreTokens(storeTokens);
                if (!storeTokens) {
                    tokenCacheHandler.deleteTokens(context);
                }
            }

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
            if (scope != null) {
                context.setScope(convertToNull(scope));
            }

            configHandler.set(context).save();
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

    private String convertToNull(String s) {
        return s.equals("null") ? null : s;
    }

    private String checkNotNull(String s) throws ConfigException {
        if (s.equals("null")) {
            throw new ConfigException("Can't clear required field");
        }
        return s;
    }

}
