package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class ConfigUpdateCommandTest {

    @Test
    @Launch(value = { "config", "update",
            "--context=context3",
            "--issuer=https://newissuer",
            "--flow=authorization-code"
    })
    public void testUpdate(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();
        Context c = ConfigHandler.get().getContext("context3");
        Assertions.assertEquals("https://newissuer", c.getIssuer());
        Assertions.assertEquals(OpenIDFlow.AUTHORIZATION_CODE, c.getFlow());
        Assertions.assertNotNull(c.getClientId());
        Assertions.assertNotNull(c.isStoreTokens());
        Assertions.assertNotNull(c.getAccessToken());
        Assertions.assertNotNull(c.getIdToken());
        Assertions.assertNotNull(c.getRefreshToken());
    }

    @Test
    @Launch(value = { "config", "update",
            "--context=context3",
            "--issuer=null",
            "--flow=null",
            "--client-id=null",
            "--store-tokens=null"
    })
    public void testClearFields(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();
        Context c = ConfigHandler.get().getContext("context3");
        Assertions.assertNull(c.getIssuer());
        Assertions.assertNull(c.getFlow());
        Assertions.assertNull(c.getClientId());
        Assertions.assertNull(c.isStoreTokens());
    }

}
