package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.commands.converter.TokenTypeConverter;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.TokenManager;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import picocli.CommandLine;

@CommandLine.Command(name = "introspect", description = "Introspects a token using the token introspection endpoint")
public class IntrospectCommand implements Runnable {

    @CommandLine.Option(names = {"--token"}, description = "Token to introspect (access, id, refresh)")
    String token;
    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextName;
    @CommandLine.Option(names = {"--type"}, description = "Token type to introspect", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;
    @CommandLine.Option(names = {"--offline"}, description = "Offline mode", defaultValue = "false")
    boolean offline;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();
            TokenManager tokenManager = new TokenManager(context, configHandler, new OpenIDClient(context));

            if (token == null) {
                token = tokenManager.getToken(tokenType, offline);
            }

            OpenIDClient client = tokenManager.getClient();
            TokenIntrospectionResponse tokenIntrospectionResponse = client.tokenIntrospectionRequest(token);

            ObjectMapper objectMapper = new ObjectMapper();
            User.cli().print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenIntrospectionResponse));
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
