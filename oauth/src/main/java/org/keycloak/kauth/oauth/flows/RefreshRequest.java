package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthGrantTypes;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.TokenResponse;

import java.io.IOException;

public class RefreshRequest extends AbstractRequest {

    private String scope;

    public RefreshRequest(OAuthRequest configuration, String scope, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, wellKnownSupplier);
        this.scope = scope;
    }

    public TokenResponse execute(String refreshToken) throws OpenIDException {
        try {
            return clientRequest(wellKnownSupplier.get().getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OAuthParams.GRANT_TYPE, OAuthGrantTypes.REFRESH_TOKEN)
                    .body(OAuthParams.REFRESH_TOKEN, refreshToken)
                    .body(OAuthParams.SCOPE, scope != null ? scope : OAuthRequest.getScope())
                    .asObject(TokenResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
