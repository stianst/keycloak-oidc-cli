package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import picocli.CommandLine;

@CommandLine.Command(name = "delete-tokens", description = "Deletes cached tokens")
public class DeleteTokensCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Delete only for a given configuration context")
    String contextName;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            if (contextName != null) {
                configHandler.deleteTokens(contextName);
            } else {
                configHandler.deleteTokens();
            }
        } catch (ConfigException e) {
            throw new CommandFailedException(e);
        }
    }

}
