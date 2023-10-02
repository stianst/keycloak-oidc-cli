package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.commands.CommandFailedException;
import org.keycloak.cli.oidc.commands.converter.OpenIDFlowConverter;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import picocli.CommandLine;

@CommandLine.Command(name = "set", description = "Creates a new configuration context")
public class ConfigSetCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context name", required = true)
    String context;

    @CommandLine.Option(names = {"--issuer"}, description = "Issuer URL", required = true)
    String iss;

    @CommandLine.Option(names = {"--flow"}, description = "Flow (authorization-code, client-credential, device, resource-owner)", required = true, converter = OpenIDFlowConverter.class)
    OpenIDFlow flow;

    @CommandLine.Option(names = {"--client-id"}, description = "Client ID")
    String clientId;

    @CommandLine.Option(names = {"--client-secret"}, description = "Client secret")
    String clientSecret;

    @CommandLine.Option(names = {"--user"}, description = "User name for resource-owner flow")
    String user;

    @CommandLine.Option(names = {"--user-password"}, description = "User password for resource-owner flow")
    String password;

    @CommandLine.Option(names = {"--store-tokens"}, description = "Store tokens", defaultValue = "true")
    boolean storeTokens;

    @Override
    public void run() {
        Context context = new Context();
        context.setName(this.context);
        context.setIssuer(iss);
        context.setFlow(flow);
        context.setClientId(clientId);
        context.setClientSecret(clientSecret);
        context.setUsername(user);
        context.setUserPassword(password);
        context.setStoreTokens(storeTokens);

        try {
            ConfigHandler.get().set(context).save();
        } catch (ConfigException e) {
            throw new CommandFailedException(e);
        }
    }

}
