package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import picocli.CommandLine;

@CommandLine.Command(name = "current", description = "Shows the current default configuration context")
public class ConfigCurrentCommand implements Runnable {

    @CommandLine.Option(names = {"--brief"}, description = "Show brief output")
    boolean brief;

    @Override
    public void run() {
        try {
            ConfigHandler.get().printCurrentContext(brief);
        } catch (ConfigException e) {
            throw new CommandFailedException(e);
        }
    }

}
