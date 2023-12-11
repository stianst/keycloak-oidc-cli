package cli.oidc;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    KeycloakContainer keycloak;

    @Override
    public Map<String, String> start() {
        keycloak = new KeycloakContainer();
        keycloak.start();

        Map<String, String> config = new HashMap<>();
        config.put("keycloak.authServerUrl", keycloak.getAuthServerUrl());
        return config;
    }

    @Override
    public void stop() {
        keycloak.stop();
    }
}
