package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.utils.Assert;

import java.io.IOException;

@QuarkusMainTest
public class ConfigCurrentCommandTest extends AbstractConfigCommandTest {


    @Test
    @Launch({ "config", "current" })
    public void testCurrent(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigCurrentCommandTest.class, "testCurrent", result);
    }

}
