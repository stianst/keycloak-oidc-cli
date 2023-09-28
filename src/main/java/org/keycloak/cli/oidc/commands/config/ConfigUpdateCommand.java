package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.Error;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Updates values for a configuration context")
public class ConfigUpdateCommand extends AbstractConfigUpdaterCommandAbstract implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name", required = true)
    String context;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();

            Context context = ConfigHandler.get().getContext(this.context);
            update(context);

            configHandler.set(context).save();
        } catch (ConfigException e) {
            Error.onError(e);
        }
    }

}
