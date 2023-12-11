package cli.oidc.commands;

import cli.oidc.utils.ConfigHandlerExtension;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.YamlTokenCacheContext;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;
import org.keycloak.client.oauth.exceptions.TokenCacheException;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class DeleteTokensCommandTest {


    @BeforeEach
    public void before() throws ConfigException, TokenCacheException {
        ConfigHandler configHandler = ConfigHandler.get();
        YamlTokenCacheHandler tokenCacheHandler = YamlTokenCacheHandler.get();

        Context context2 = configHandler.getContext("context2");
        YamlTokenCacheContext tokenCacheContext2 = tokenCacheHandler.getTokenCacheContext(context2);
        tokenCacheContext2.setRefreshToken("mytoken");
        tokenCacheContext2.setAccessToken("mytoken");
        tokenCacheContext2.setIdToken("mytoken");

        Context context3 = configHandler.getContext("context3");
        YamlTokenCacheContext tokenCacheContext3 = tokenCacheHandler.getTokenCacheContext(context3);
        tokenCacheContext3.setRefreshToken("mytoken");
        tokenCacheContext3.setAccessToken("mytoken");
        tokenCacheContext3.setIdToken("mytoken");

        tokenCacheHandler.save();
    }

    @Test
    @Launch({ "delete-tokens" })
    public void testDeleteTokens(LaunchResult result) throws TokenCacheException, ConfigException {
        ConfigHandler configHandler = ConfigHandler.get();
        YamlTokenCacheHandler tokenCacheHandler = YamlTokenCacheHandler.get();
        tokenCacheHandler.reload();

        Context context2 = configHandler.getContext("context2");
        YamlTokenCacheContext tokenCacheContext2 = tokenCacheHandler.getTokenCacheContext(context2);
        Assertions.assertNull(tokenCacheContext2.getRefreshToken());
        Assertions.assertNull(tokenCacheContext2.getAccessToken());
        Assertions.assertNull(tokenCacheContext2.getIdToken());

        Context context3 = configHandler.getContext("context3");
        YamlTokenCacheContext tokenCacheContext3 = tokenCacheHandler.getTokenCacheContext(context3);
        Assertions.assertNull(tokenCacheContext3.getRefreshToken());
        Assertions.assertNull(tokenCacheContext3.getAccessToken());
        Assertions.assertNull(tokenCacheContext3.getIdToken());
    }

    @Test
    @Launch({ "delete-tokens", "--context=context2" })
    public void testDeleteTokenForSingleContext(LaunchResult result) throws ConfigException, TokenCacheException {
        ConfigHandler configHandler = ConfigHandler.get();
        YamlTokenCacheHandler tokenCacheHandler = YamlTokenCacheHandler.get();
        tokenCacheHandler.reload();

        Context context2 = configHandler.getContext("context2");
        YamlTokenCacheContext tokenCacheContext2 = tokenCacheHandler.getTokenCacheContext(context2);
        Assertions.assertNull(tokenCacheContext2.getRefreshToken());
        Assertions.assertNull(tokenCacheContext2.getAccessToken());
        Assertions.assertNull(tokenCacheContext2.getIdToken());

        Context context3 = configHandler.getContext("context3");
        YamlTokenCacheContext tokenCacheContext3 = tokenCacheHandler.getTokenCacheContext(context3);
        Assertions.assertNotNull(tokenCacheContext3.getRefreshToken());
        Assertions.assertNotNull(tokenCacheContext3.getAccessToken());
        Assertions.assertNotNull(tokenCacheContext3.getIdToken());
    }

}
