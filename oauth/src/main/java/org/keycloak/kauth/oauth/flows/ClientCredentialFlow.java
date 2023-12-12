package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthGrantTypes;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.TokenResponse;

public class ClientCredentialFlow extends AbstractFlow {

    public ClientCredentialFlow(OAuthRequest configuration, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, scope, wellKnownSupplier);
    }

    @Override
    public TokenResponse execute() throws TokenRequestFailure {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OAuthParams.GRANT_TYPE, OAuthGrantTypes.CLIENT_CREDENTIAL)
                    .body(OAuthParams.SCOPE, getScope())
                    .asObject(TokenResponse.class);
        } catch (Exception e) {
            throw new TokenRequestFailure(e);
        }
    }
}
