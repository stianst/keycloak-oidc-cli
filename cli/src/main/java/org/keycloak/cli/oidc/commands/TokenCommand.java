package org.keycloak.cli.oidc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.cli.oidc.commands.converter.ContextConverter;
import org.keycloak.cli.oidc.commands.converter.TokenTypeConverter;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheWrapper;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;
import org.keycloak.cli.oidc.kubectl.ExecCredentialRepresentation;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.TokenManager;
import org.keycloak.kauth.oauth.TokenParser;
import org.keycloak.kauth.oauth.TokenType;
import org.keycloak.kauth.oauth.User;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenCacheException;
import org.keycloak.kauth.oauth.exceptions.TokenManagerException;
import picocli.CommandLine;

@CommandLine.Command(name = "token", description = "Returns a token")
public class TokenCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to use")
    String contextName;
    @CommandLine.Option(names = {"--type"}, description = "Token type to return (access, id, refresh)", defaultValue = "access", converter = TokenTypeConverter.class)
    TokenType tokenType;
    @CommandLine.Option(names = {"-s", "--scope"}, description = "Request a specific scope")
    String scope;
    @CommandLine.Option(names = {"--decode"}, description = "Decode token", defaultValue = "false")
    boolean decode;
    @CommandLine.Option(names = {"--offline"}, description = "Offline mode", defaultValue = "false")
    boolean offline;
    @CommandLine.Option(names = {"--kubectl"}, description = "Kubectl mode", defaultValue = "false")
    boolean kubectl;
    @CommandLine.Option(names = {"--refresh"}, description = "Update tokens ", defaultValue = "false")
    boolean refresh;

    public static void main(String[] args) {
        TokenCommand tokenCommand = new TokenCommand();
        tokenCommand.tokenType = TokenType.ACCESS;
        tokenCommand.decode = true;
        tokenCommand.refresh = true;
        tokenCommand.run();
    }

    @Override
    public void run() {
        if (!kubectl && System.getenv().containsKey("KUBERNETES_EXEC_INFO")) {
            kubectl = true;
        }

        try {
            String token = getToken(tokenType);

            if (kubectl) {
                ExecCredentialRepresentation execCredential = new ExecCredentialRepresentation();
                execCredential.getStatus().setToken(token);

                // TODO Maybe set expiration time, not sure, as we're caching tokens anyways
                // TODO JWT jwt = TokenParser.parse(token).getJWT();
                // TODO execCredential.getStatus().setExpirationTimestamp();

                ObjectMapper objectMapper = new ObjectMapper();
                User.cli().print(objectMapper.writeValueAsString(execCredential));
            } else if (decode) {
                String decoded = TokenParser.parse(token).getClaimsDecoded();
                User.cli().print(decoded);
            } else {
                User.cli().print(token);
            }
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

    private String getToken(TokenType tokenType) throws OpenIDException, ConfigException, TokenManagerException, TokenCacheException {
        ConfigHandler configHandler = ConfigHandler.get();
        Context context = contextName != null ? configHandler.getContext(contextName) : configHandler.getCurrentContext();
        TokenCacheWrapper tokenCacheWrapper = null;
        if (context.isStoreTokens()) {
            YamlTokenCacheHandler yamlTokenCacheHandler = YamlTokenCacheHandler.get();
            tokenCacheWrapper = new TokenCacheWrapper(yamlTokenCacheHandler, yamlTokenCacheHandler.getTokenCacheContext(context));
        }
        TokenManager tokenManager = new TokenManager(ContextConverter.toRequest(context), tokenCacheWrapper, new OAuthClient(ContextConverter.toRequest(context)));
        return tokenManager.getToken(tokenType, scope, refresh, offline);
    }

}
