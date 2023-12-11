package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.cli.oidc.commands.converter.ContextConverter;
import org.keycloak.cli.oidc.commands.converter.TokenTypeConverter;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheWrapper;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.TokenManager;
import org.keycloak.client.oauth.TokenType;
import org.keycloak.client.oauth.User;
import org.keycloak.client.oauth.representations.TokenIntrospectionResponse;
import picocli.CommandLine;

@CommandLine.Command(name = "introspect", description = "Introspects a token using the token introspection endpoint")
public class IntrospectCommand implements Runnable {

    @CommandLine.Option(names = {"--token"}, description = "Token to introspect (access, id, refresh)")
    String token;
    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextName;
    @CommandLine.Option(names = {"--type"}, description = "Token type to introspect", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            YamlTokenCacheHandler yamlTokenCacheHandler = YamlTokenCacheHandler.get();
            Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();
            TokenCacheWrapper tokenCacheWrapper = new TokenCacheWrapper(yamlTokenCacheHandler, yamlTokenCacheHandler.getTokenCacheContext(context));

            OpenIDClient client = new OpenIDClient(ContextConverter.toRequest(context));
            if (token == null) {
                TokenManager tokenManager = new TokenManager(ContextConverter.toRequest(context), tokenCacheWrapper, client);
                token = tokenManager.getSaved(tokenType);
            }

            TokenIntrospectionResponse tokenIntrospectionResponse = client.tokenIntrospectionRequest(token);

            ObjectMapper objectMapper = new ObjectMapper();
            User.cli().print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenIntrospectionResponse));
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
