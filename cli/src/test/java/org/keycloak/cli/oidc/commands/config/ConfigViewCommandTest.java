package org.keycloak.cli.oidc.commands.config;

import org.keycloak.cli.oidc.utils.Assert;
import org.keycloak.cli.oidc.utils.ConfigHandlerExtension;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class ConfigViewCommandTest {

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
