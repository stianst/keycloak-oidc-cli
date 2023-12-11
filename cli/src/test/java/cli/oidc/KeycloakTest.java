package cli.oidc;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
//@QuarkusTestResource(KeycloakTestResource.class)
// https://github.com/docker-java/docker-java/issues/2201
// https://github.com/containers/crun/issues/1302
// Need Podman >=4.6.3?!
@Disabled
public class KeycloakTest {

//    @ConfigProperty(name = "keycloak.authServerUrl")
    private String authServerUrl;

    @Test
    public void hello() {
        System.getenv().entrySet().stream().forEach(e -> System.out.println(e.getKey() + " --> " + e.getValue()));
        System.out.println(authServerUrl);
    }

}
