package org.keycloak.cli.oidc.utils;

import io.quarkus.test.junit.main.LaunchResult;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Assert {

    public static void expectedOutput(Class clazz, String method, LaunchResult result) throws IOException {
        String expectedOutput = new String(clazz.getResource(clazz.getSimpleName() + "-" + method + ".txt").openStream().readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals(expectedOutput, result.getOutput());
    }

}
