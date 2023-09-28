package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenIntrospectionResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

import java.io.IOException;

public class TokenIntrospectionRequest extends AbstractRequest {

    public TokenIntrospectionRequest(Context configuration, WellKnown wellKnown) {
        super(configuration, wellKnown);
    }

    public TokenIntrospectionResponse execute(String token) throws OpenIDException {
        try {
            return clientRequest(wellKnown.getIntrospectionEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body("token", token)
                    .asObject(TokenIntrospectionResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
