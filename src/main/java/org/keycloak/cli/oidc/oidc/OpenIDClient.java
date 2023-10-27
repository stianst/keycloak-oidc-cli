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
import org.keycloak.cli.oidc.oidc.flows.UserInfoRequest;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.UserInfoResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

import java.io.IOException;

public class OpenIDClient {

    private Context context;

    private WellKnownSupplier wellKnownSupplier;

    public OpenIDClient(Context context) throws OpenIDException {
        this.context = context;
        this.wellKnownSupplier = new WellKnownSupplier();
    }

    public TokenResponse tokenRequest() throws OpenIDException {
        return tokenRequest(null);
    }

    public TokenResponse tokenRequest(String scope) throws OpenIDException {
        AbstractFlow flow;
        switch (context.getFlow()) {
            case AUTHORIZATION_CODE:
                flow = new AuthorizationCodeFlow(context, scope, wellKnownSupplier);
                break;
            case RESOURCE_OWNER:
                flow = new ResourceOwnerFlow(context, scope, wellKnownSupplier);
                break;
            case DEVICE:
                flow = new DeviceFlow(context, scope, wellKnownSupplier);
                break;
            case CLIENT_CREDENTIAL:
                flow = new ClientCredentialFlow(context, scope, wellKnownSupplier);
                break;
            default:
                throw new RuntimeException("Unknown flow");
        }

        TokenResponse tokenResponse = flow.execute();
        return checkError(tokenResponse);
    }

    public TokenResponse refreshRequest(String refreshToken) throws OpenIDException {
        return refreshRequest(refreshToken, null);
    }

    public TokenResponse refreshRequest(String refreshToken, String scope) throws OpenIDException {
        TokenResponse tokenResponse = new RefreshRequest(context, scope, wellKnownSupplier)
                .execute(refreshToken);
        return checkError(tokenResponse);
    }

    public TokenIntrospectionResponse tokenIntrospectionRequest(String token) throws OpenIDException {
        TokenIntrospectionResponse tokenIntrospectionResponse = new TokenIntrospectionRequest(context, wellKnownSupplier)
                .execute(token);
        return tokenIntrospectionResponse;
    }

    public UserInfoResponse userInfoRequest(String token) throws OpenIDException {
        UserInfoResponse userInfoResponse = new UserInfoRequest(context, wellKnownSupplier)
                .execute(token);
        return userInfoResponse;
    }

    private TokenResponse checkError(TokenResponse tokenResponse) throws OpenIDException {
        if (tokenResponse.getError() != null) {
            throw new OpenIDException("Token request failed: " + tokenResponse.getError());
        }
        return tokenResponse;
    }

    public class WellKnownSupplier {

        private WellKnown wellKnown;

        public WellKnown get() throws OpenIDException {
            if (wellKnown == null) {
                try {
                    wellKnown = Http.create(context.getIssuer() + "/.well-known/openid-configuration")
                            .userAgent("kc-oidc/1.0")
                            .accept(MimeType.JSON)
                            .asObject(WellKnown.class);
                } catch (IOException e) {
                    throw new OpenIDException("Failed to retrieve well-known endpoint", e);
                }
            }
            return wellKnown;
        }
    }

}
