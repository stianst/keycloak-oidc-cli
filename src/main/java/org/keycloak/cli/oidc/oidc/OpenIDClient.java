package org.keycloak.cli.oidc.oidc;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.http.client.Http;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.flows.AbstractFlow;
import org.keycloak.cli.oidc.oidc.flows.AuthorizationCodeFlow;
import org.keycloak.cli.oidc.oidc.flows.ClientCredentialFlow;
import org.keycloak.cli.oidc.oidc.flows.DeviceFlow;
import org.keycloak.cli.oidc.oidc.flows.RefreshRequest;
import org.keycloak.cli.oidc.oidc.flows.ResourceOwnerFlow;
import org.keycloak.cli.oidc.oidc.flows.TokenIntrospectionRequest;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

import java.io.IOException;

public class OpenIDClient {

    private Context context;
    private WellKnown wellKnown;

    public OpenIDClient(Context context) throws OpenIDException {
        this.context = context;

        try {
            wellKnown = Http.create(context.getIssuer() + "/.well-known/openid-configuration")
                    .userAgent("kc-oidc/1.0")
                    .accept(MimeType.JSON)
                    .asObject(WellKnown.class);
        } catch (IOException e) {
            throw new OpenIDException("Failed to retrieve well-known endpoint", e);
        }
    }

    public TokenResponse tokenRequest() throws OpenIDException {
        AbstractFlow flow;
        switch (context.getFlow()) {
            case AUTHORIZATION_CODE:
                flow = new AuthorizationCodeFlow(context, wellKnown);
                break;
            case RESOURCE_OWNER:
                flow = new ResourceOwnerFlow(context, wellKnown);
                break;
            case DEVICE:
                flow = new DeviceFlow(context, wellKnown);
                break;
            case CLIENT_CREDENTIAL:
                flow = new ClientCredentialFlow(context, wellKnown);
                break;
            default:
                throw new RuntimeException("Unknown flow");
        }

        TokenResponse tokenResponse = flow.execute();
        return checkError(tokenResponse);
    }

    public TokenResponse refreshRequest(String refreshToken) throws OpenIDException {
        TokenResponse tokenResponse = new RefreshRequest(context, wellKnown)
                .execute(refreshToken);
        return checkError(tokenResponse);
    }

    public TokenIntrospectionResponse tokenIntrospectionRequest(String token) throws OpenIDException {
        TokenIntrospectionResponse tokenIntrospectionResponse = new TokenIntrospectionRequest(context, wellKnown)
                .execute(token);
        return tokenIntrospectionResponse;
    }

    private TokenResponse checkError(TokenResponse tokenResponse) throws OpenIDException {
        if (tokenResponse.getError() != null) {
            throw new OpenIDException("Token request failed: " + tokenResponse.getError());
        }
        return tokenResponse;
    }

}
