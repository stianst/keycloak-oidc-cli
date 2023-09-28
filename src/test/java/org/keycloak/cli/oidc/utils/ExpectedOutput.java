package org.keycloak.cli.oidc.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExpectedOutput {

    public static String getExpectedOutput(Class clazz, String method) throws IOException {
        return new String(clazz.getResource(clazz.getSimpleName() + "-" + method + ".txt").openStream().readAllBytes(), StandardCharsets.UTF_8);
    }

}
