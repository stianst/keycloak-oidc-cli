package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class ConfigDeleteCommandTest {

    @Test
    @Launch({ "config", "delete", "--context=context3" })
    public void testDelete(LaunchResult result) throws ConfigException {
        ConfigHandler.get().reload();

        ConfigException exception = Assertions.assertThrows(ConfigException.class, () -> ConfigHandler.get().getContext("context3"));
        Assertions.assertEquals("Context 'context3' not found", exception.getMessage());
    }

}
