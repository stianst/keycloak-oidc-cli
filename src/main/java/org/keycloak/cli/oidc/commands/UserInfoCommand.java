package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.TokenManager;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.representations.UserInfoResponse;
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
            TokenCacheHandler tokenCacheHandler = TokenCacheHandler.get();
            Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();

            OpenIDClient client = new OpenIDClient(context);
            if (token == null) {
                TokenManager tokenManager = new TokenManager(context, tokenCacheHandler, client);
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
