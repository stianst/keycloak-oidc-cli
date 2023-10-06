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
    }

    @Test
    @Launch(value = { "config", "update",
            "--context=context3",
            "--client-id=null"
    })
    public void testClearFields(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();
        Context c = ConfigHandler.get().getContext("context3");
        Assertions.assertNull(c.getClientId());
    }

    @Test
    @Launch(value = { "config", "update",
            "--context=context3",
            "--issuer=null"
    }, exitCode = 1)
    public void testTryToClearIss(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();
        Context c = ConfigHandler.get().getContext("context3");
        Assertions.assertNotNull(c.getIssuer());
        Assertions.assertEquals("Can't clear required field", result.getErrorStream().get(0));
    }

}
