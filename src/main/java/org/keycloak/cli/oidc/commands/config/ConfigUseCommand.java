package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import picocli.CommandLine;

@CommandLine.Command(name = "use", description = "Set the current default configuration context")
public class ConfigUseCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name", required = true)
    String context;

    @Override
    public void run() {
        try {
            ConfigHandler.get().setCurrent(context).save();
        } catch (ConfigException e) {
            throw new CommandFailedException(e);
        }
    }

}
