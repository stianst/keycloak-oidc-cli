package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import picocli.CommandLine;

@CommandLine.Command(name = "delete-tokens", description = "Deletes cached tokens")
public class DeleteTokensCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Delete only for a given configuration context")
    String contextName;

    @Override
    public void run() {
        try {
            if (contextName != null) {
                ConfigHandler.get().deleteTokens(contextName);
            } else {
                ConfigHandler.get().deleteTokens();
            }
        } catch (ConfigException e) {
            Error.onError(e);
        }
    }

}
