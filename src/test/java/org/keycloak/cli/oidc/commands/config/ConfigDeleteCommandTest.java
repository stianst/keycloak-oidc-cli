package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;

@QuarkusMainTest
public class ConfigDeleteCommandTest extends AbstractConfigCommandTest {

    @Test
    @Launch({ "config", "delete", "--context=context3" })
    public void testCurrent(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();
        Assertions.assertEquals(null, ConfigHandler.get().getContext("context3"));
    }

}
