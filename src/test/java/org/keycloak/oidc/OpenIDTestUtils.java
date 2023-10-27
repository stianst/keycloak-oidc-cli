package org.keycloak.oidc;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;

public class OpenIDTestUtils {

    public static Context createContext(String issuerUrl, OpenIDFlow flow) {
        Context context = new Context();
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
