package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;

import java.io.IOException;

public class TokenIntrospectionRequest extends AbstractRequest {

    public TokenIntrospectionRequest(Context configuration, OpenIDClient.WellKnownSupplier wellKnownSupplier) {
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
