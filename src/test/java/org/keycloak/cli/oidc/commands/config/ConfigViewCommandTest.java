package org.keycloak.cli.oidc.commands.config;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.keycloak.cli.oidc.utils.Assert;

import java.io.IOException;

@QuarkusMainTest
public class ConfigViewCommandTest extends AbstractConfigCommandTest {

    @Test
    @Launch({ "config", "view" })
    public void testViewAll(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigViewCommandTest.class, "testViewAll", result);
    }

    @Test
    @Launch({ "config", "view", "--context=context3" })
    public void testViewContext3(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigViewCommandTest.class, "testViewContext3", result);
    }

    @Test
    @Launch({ "config", "view", "--brief" })
    public void testViewBrief(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigViewCommandTest.class, "testViewBrief", result);
    }

}
