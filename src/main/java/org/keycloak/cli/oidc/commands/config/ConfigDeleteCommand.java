package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = "Deletes a configuration context")
public class ConfigDeleteCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name", required = true)
    String contextName;

    @Override
    public void run() {
        try {
            Context context = ConfigHandler.get().getContext(contextName);
            ConfigHandler.get().delete(contextName).save();
            TokenCacheHandler.get().deleteTokens(context);
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
