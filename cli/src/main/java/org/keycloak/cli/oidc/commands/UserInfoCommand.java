package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.cli.oidc.commands.converter.ContextConverter;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheWrapper;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.TokenManager;
import org.keycloak.kauth.oauth.TokenType;
import org.keycloak.kauth.oauth.User;
import org.keycloak.kauth.oauth.representations.UserInfoResponse;
import picocli.CommandLine;

@CommandLine.Command(name = "userinfo", description = "Returns claims about the authenticated user")
public class UserInfoCommand implements Runnable {

    @CommandLine.Option(names = {"--token"}, description = "Token to introspect (access, id, refresh)")
    String token;
    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextName;

    public static void main(String[] args) {
        UserInfoCommand userInfoCommand = new UserInfoCommand();
        userInfoCommand.run();
    }

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();

            TokenCacheWrapper tokenCacheWrapper = null;
            if (context.isStoreTokens()) {
                YamlTokenCacheHandler yamlTokenCacheHandler = YamlTokenCacheHandler.get();
                tokenCacheWrapper = new TokenCacheWrapper(yamlTokenCacheHandler, yamlTokenCacheHandler.getTokenCacheContext(context));
            }

            OAuthClient client = new OAuthClient(ContextConverter.toRequest(context));
            if (token == null) {
                TokenManager tokenManager = new TokenManager(ContextConverter.toRequest(context), tokenCacheWrapper, client);
                token = tokenManager.getSaved(TokenType.ACCESS);
            }

            UserInfoResponse userInfoResponse = client.userInfoRequest(token);

            ObjectMapper objectMapper = new ObjectMapper();
            User.cli().print(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userInfoResponse));
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
