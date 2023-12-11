package org.keycloak.client.oauth.flows;

import org.keycloak.client.http.MimeType;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDParams;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenRequestFailure;
import org.keycloak.client.oauth.representations.TokenIntrospectionResponse;

import java.io.IOException;

public class TokenIntrospectionRequest extends AbstractRequest {

    public TokenIntrospectionRequest(OpenIDRequest configuration, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
        super(configuration, wellKnownSupplier);
    }

    public TokenIntrospectionResponse execute(String token) throws OpenIDException {
        try {
            return clientRequest(wellKnownSupplier.get().getIntrospectionEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.TOKEN, token)
                    .asObject(TokenIntrospectionResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
