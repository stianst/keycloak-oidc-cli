package oidc;

import org.keycloak.kauth.oauth.OAuthFlow;
import org.keycloak.kauth.oauth.OAuthRequest;

public class OpenIDTestUtils {

    public static OAuthRequest createContext(String issuerUrl, OAuthFlow flow) {
        OAuthRequest context = new OAuthRequest();
        context.setIssuer(issuerUrl);
        context.setFlow(flow);
        context.setClientId("theclient");
        context.setClientSecret("thesecret");
        context.setUsername("theuser");
        context.setUserPassword("thepassword");
        context.setScope("openid");
        return context;
    }

}
