package org.keycloak.kauth.oauth;

import org.keycloak.kauth.http.Http;
import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.flows.AbstractFlow;
import org.keycloak.kauth.oauth.flows.AuthorizationCodeFlow;
import org.keycloak.kauth.oauth.flows.ClientCredentialFlow;
import org.keycloak.kauth.oauth.flows.DeviceFlow;
import org.keycloak.kauth.oauth.flows.RefreshRequest;
import org.keycloak.kauth.oauth.flows.ResourceOwnerFlow;
import org.keycloak.kauth.oauth.flows.TokenIntrospectionRequest;
import org.keycloak.kauth.oauth.flows.UserInfoRequest;
import org.keycloak.kauth.oauth.representations.TokenIntrospectionResponse;
import org.keycloak.kauth.oauth.representations.TokenResponse;
import org.keycloak.kauth.oauth.representations.UserInfoResponse;
import org.keycloak.kauth.oauth.representations.WellKnown;

import java.io.IOException;

public class OAuthClient {

    private OAuthRequest OAuthRequest;

    private WellKnownSupplier wellKnownSupplier;

    public OAuthClient(OAuthRequest OAuthRequest) throws OpenIDException {
        this.OAuthRequest = OAuthRequest;
        this.wellKnownSupplier = new WellKnownSupplier();
    }

    public TokenResponse tokenRequest() throws OpenIDException {
        return tokenRequest(null);
    }

    public TokenResponse tokenRequest(String scope) throws OpenIDException {
        AbstractFlow flow;
        switch (OAuthRequest.getFlow()) {
            case AUTHORIZATION_CODE:
                flow = new AuthorizationCodeFlow(OAuthRequest, scope, wellKnownSupplier);
                break;
            case RESOURCE_OWNER:
                flow = new ResourceOwnerFlow(OAuthRequest, scope, wellKnownSupplier);
                break;
            case DEVICE:
                flow = new DeviceFlow(OAuthRequest, scope, wellKnownSupplier);
                break;
            case CLIENT_CREDENTIAL:
                flow = new ClientCredentialFlow(OAuthRequest, scope, wellKnownSupplier);
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
        TokenResponse tokenResponse = new RefreshRequest(OAuthRequest, scope, wellKnownSupplier)
                .execute(refreshToken);
        return checkError(tokenResponse);
    }

    public TokenIntrospectionResponse tokenIntrospectionRequest(String token) throws OpenIDException {
        TokenIntrospectionResponse tokenIntrospectionResponse = new TokenIntrospectionRequest(OAuthRequest, wellKnownSupplier)
                .execute(token);
        return tokenIntrospectionResponse;
    }

    public UserInfoResponse userInfoRequest(String token) throws OpenIDException {
        UserInfoResponse userInfoResponse = new UserInfoRequest(OAuthRequest, wellKnownSupplier)
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
                    wellKnown = Http.create(OAuthRequest.getIssuer() + "/.well-known/openid-configuration")
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
