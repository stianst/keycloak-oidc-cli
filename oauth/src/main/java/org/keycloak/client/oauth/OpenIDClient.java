package org.keycloak.client.oauth;

import org.keycloak.client.http.Http;
import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.flows.AbstractFlow;
import org.keycloak.client.oauth.flows.AuthorizationCodeFlow;
import org.keycloak.client.oauth.flows.ClientCredentialFlow;
import org.keycloak.client.oauth.flows.DeviceFlow;
import org.keycloak.client.oauth.flows.RefreshRequest;
import org.keycloak.client.oauth.flows.ResourceOwnerFlow;
import org.keycloak.client.oauth.flows.TokenIntrospectionRequest;
import org.keycloak.client.oauth.flows.UserInfoRequest;
import org.keycloak.client.oauth.representations.TokenIntrospectionResponse;
import org.keycloak.client.oauth.representations.TokenResponse;
import org.keycloak.client.oauth.representations.UserInfoResponse;
import org.keycloak.client.oauth.representations.WellKnown;

import java.io.IOException;

public class OpenIDClient {

    private OpenIDRequest openIDRequest;

    private WellKnownSupplier wellKnownSupplier;

    public OpenIDClient(OpenIDRequest openIDRequest) throws OpenIDException {
        this.openIDRequest = openIDRequest;
        this.wellKnownSupplier = new WellKnownSupplier();
    }

    public TokenResponse tokenRequest() throws OpenIDException {
        return tokenRequest(null);
    }

    public TokenResponse tokenRequest(String scope) throws OpenIDException {
        AbstractFlow flow;
        switch (openIDRequest.getFlow()) {
            case AUTHORIZATION_CODE:
                flow = new AuthorizationCodeFlow(openIDRequest, scope, wellKnownSupplier);
                break;
            case RESOURCE_OWNER:
                flow = new ResourceOwnerFlow(openIDRequest, scope, wellKnownSupplier);
                break;
            case DEVICE:
                flow = new DeviceFlow(openIDRequest, scope, wellKnownSupplier);
                break;
            case CLIENT_CREDENTIAL:
                flow = new ClientCredentialFlow(openIDRequest, scope, wellKnownSupplier);
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
        TokenResponse tokenResponse = new RefreshRequest(openIDRequest, scope, wellKnownSupplier)
                .execute(refreshToken);
        return checkError(tokenResponse);
    }

    public TokenIntrospectionResponse tokenIntrospectionRequest(String token) throws OpenIDException {
        TokenIntrospectionResponse tokenIntrospectionResponse = new TokenIntrospectionRequest(openIDRequest, wellKnownSupplier)
                .execute(token);
        return tokenIntrospectionResponse;
    }

    public UserInfoResponse userInfoRequest(String token) throws OpenIDException {
        UserInfoResponse userInfoResponse = new UserInfoRequest(openIDRequest, wellKnownSupplier)
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
                    wellKnown = Http.create(openIDRequest.getIssuer() + "/.well-known/openid-configuration")
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
