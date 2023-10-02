package org.keycloak.cli.oidc.commands;

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
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class DeleteTokensCommandTest {


    @BeforeEach
    public void before(@ConfigHandlerExtension.Config ConfigHandler configHandler) throws ConfigException {
        Context context2 = configHandler.getContext("context2");
        context2.setRefreshToken("mytoken");
        context2.setAccessToken("mytoken");
        context2.setIdToken("mytoken");

        Context context3 = configHandler.getContext("context3");
        context3.setRefreshToken("mytoken");
        context3.setAccessToken("mytoken");
        context3.setIdToken("mytoken");

        configHandler.save();
    }

    @Test
    @Launch({ "delete-tokens" })
    public void testDeleteTokens(@ConfigHandlerExtension.Config ConfigHandler configHandler, LaunchResult result) throws ConfigException {
        configHandler.reload();

        Context context2 = configHandler.getContext("context2");
        Assertions.assertNull(context2.getRefreshToken());
        Assertions.assertNull(context2.getAccessToken());
        Assertions.assertNull(context2.getIdToken());

        Context context3 = configHandler.getContext("context3");
        Assertions.assertNull(context3.getRefreshToken());
        Assertions.assertNull(context3.getAccessToken());
        Assertions.assertNull(context3.getIdToken());
    }

    @Test
    @Launch({ "delete-tokens", "--context=context2" })
    public void testDeleteTokenForSingleContext(@ConfigHandlerExtension.Config ConfigHandler configHandler, LaunchResult result) throws ConfigException {
        configHandler.reload();

        Context context2 = configHandler.getContext("context2");
        Assertions.assertNull(context2.getRefreshToken());
        Assertions.assertNull(context2.getAccessToken());
        Assertions.assertNull(context2.getIdToken());

        Context context3 = configHandler.getContext("context3");
        Assertions.assertNotNull(context3.getRefreshToken());
        Assertions.assertNotNull(context3.getAccessToken());
        Assertions.assertNotNull(context3.getIdToken());
    }

}
