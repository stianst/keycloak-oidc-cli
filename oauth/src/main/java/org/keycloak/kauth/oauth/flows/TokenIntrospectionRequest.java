package org.keycloak.kauth.oauth.flows;

import org.keycloak.kauth.http.MimeType;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthParams;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenRequestFailure;
import org.keycloak.kauth.oauth.representations.TokenIntrospectionResponse;

import java.io.IOException;

public class TokenIntrospectionRequest extends AbstractRequest {

    public TokenIntrospectionRequest(OAuthRequest configuration, OAuthClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, wellKnownSupplier);
    }

    public TokenIntrospectionResponse execute(String token) throws OpenIDException {
        try {
            return clientRequest(wellKnownSupplier.get().getIntrospectionEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OAuthParams.TOKEN, token)
                    .asObject(TokenIntrospectionResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
