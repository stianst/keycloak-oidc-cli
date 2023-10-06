package org.keycloak.cli.oidc.commands.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import picocli.CommandLine;

@CommandLine.Command(name = "current", description = "Shows the current default configuration context")
public class ConfigCurrentCommand implements Runnable {

    @CommandLine.Option(names = {"--brief"}, description = "Show brief output")
    boolean brief;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            Context context = ConfigCopy.copy(configHandler.getCurrentContext(), brief);

            User.cli().print(objectMapper.writeValueAsString(context));
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
