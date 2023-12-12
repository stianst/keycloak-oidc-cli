package org.keycloak.cli.oidc.commands;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@QuarkusMainTest
public class VersionTest {

    String VERSION_PATTERN = "[0-9]+[.][0-9]+[.][0-9]+(-SNAPSHOT){0,1}";

    @Test
    @Launch({ "--version" })
    public void testDecode(LaunchResult result) throws IOException {
        Assertions.assertTrue(result.getOutput().matches(VERSION_PATTERN));
    }

}
