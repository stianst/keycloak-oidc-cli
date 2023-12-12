package org.keycloak.cli.oidc.commands.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.config.Config;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.kauth.oauth.User;
import picocli.CommandLine;

@CommandLine.Command(name = "view", description = "View all, or a specific, configuration contexts")
public class ConfigViewCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name")
    String contextName;
    @CommandLine.Option(names = {"--brief"}, description = "Show brief output")
    boolean brief;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            if (contextName != null) {
                Context context = ConfigCopy.copy(configHandler.getContext(contextName), brief);
                User.cli().print(objectMapper.writeValueAsString(context));
            } else {
                Config config = ConfigCopy.copy(configHandler.getConfig(), brief);
                User.cli().print(objectMapper.writeValueAsString(config));
            }
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
