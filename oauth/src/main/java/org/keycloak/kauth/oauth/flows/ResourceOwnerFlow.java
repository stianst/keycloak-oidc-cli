package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthGrantTypes;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.TokenResponse;

public class ResourceOwnerFlow extends AbstractFlow {

    public ResourceOwnerFlow(OAuthRequest configuration, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    @Override
    public TokenResponse execute() throws TokenRequestFailure {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OAuthParams.GRANT_TYPE, OAuthGrantTypes.PASSWORD)
                    .body(OAuthParams.SCOPE, getScope())
                    .body(OAuthParams.USERNAME, OAuthRequest.getUsername())
                    .body(OAuthParams.PASSWORD, OAuthRequest.getUserPassword())
                    .asObject(TokenResponse.class);
        } catch (Exception e) {
            throw new TokenRequestFailure(e);
        }
    }
}
