package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;

@QuarkusMainTest
public class ConfigSetCommandTest extends AbstractConfigCommandTest {

    @Test
    @Launch({ "config", "set",
            "--context=context4",
            "--issuer=https://theissuer",
            "--flow=device",
            "--client-id=theclient",
            "--client-secret=thesecret",
            "--user=theuser",
            "--user-password=thepassword",
            "--store-tokens=false"
    })
    public void testSet(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();

        Context c = ConfigHandler.get().getContext("context4");
        Assertions.assertEquals("https://theissuer", c.getIssuer());
        Assertions.assertEquals(OpenIDFlow.DEVICE, c.getFlow());
        Assertions.assertEquals("theclient", c.getClientId());
        Assertions.assertEquals("thesecret", c.getClientSecret());
        Assertions.assertEquals("theuser", c.getUsername());
        Assertions.assertEquals("thepassword", c.getUserPassword());
    }

    @Test
    @Launch({ "config", "set",
            "--context=context3",
            "--issuer=https://theissuer",
            "--flow=authorization-code"
    })
    public void testOverwriteExisting(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();

        Context c = ConfigHandler.get().getContext("context3");
        Assertions.assertEquals("https://theissuer", c.getIssuer());
        Assertions.assertEquals(OpenIDFlow.AUTHORIZATION_CODE, c.getFlow());
    }

    @Test
    @Launch(value = { "config", "set",
            "--context=context4",
            "--issuer=https://theissuer",
            "--flow=invalid"
    }, exitCode = 2)
    public void testInvalidFlow(LaunchResult result) throws ConfigException {
        Assertions.assertEquals("Invalid value for option '--flow': valid values: authorization-code, client-credential, device, resource-owner", result.getErrorStream().get(0));
    }

}
