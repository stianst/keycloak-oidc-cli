package cli.oidc.commands.config;

import cli.oidc.utils.Assert;
import cli.oidc.utils.ConfigHandlerExtension;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@QuarkusMainTest
@ExtendWith(ConfigHandlerExtension.class)
public class ConfigCurrentCommandTest {

    @Test
    @Launch({ "config", "current" })
    public void testCurrent(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigCurrentCommandTest.class, "testCurrent", result);
    }

    @Test
    @Launch({ "config", "current", "--brief" })
    public void testBrief(LaunchResult result) throws IOException {
        Assert.expectedOutput(ConfigCurrentCommandTest.class, "testBrief", result);
    }

}
