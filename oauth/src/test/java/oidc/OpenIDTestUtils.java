package oidc;

import org.keycloak.client.oauth.OpenIDFlow;
import org.keycloak.client.oauth.OpenIDRequest;

public class OpenIDTestUtils {

    public static OpenIDRequest createContext(String issuerUrl, OpenIDFlow flow) {
        OpenIDRequest context = new OpenIDRequest();
        context.setIssuer(issuerUrl);
        context.setFlow(flow);
        context.setClientId("theclient");
        context.setClientSecret("thesecret");
        context.setUsername("theuser");
        context.setUserPassword("thepassword");
        context.setStoreTokens(false);
        context.setScope("openid");
        return context;
    }

}
