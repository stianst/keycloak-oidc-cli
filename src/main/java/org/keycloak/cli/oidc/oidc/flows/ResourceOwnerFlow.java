package org.keycloak.cli.oidc.oidc.flows;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.http.MimeType;
import org.keycloak.cli.oidc.oidc.OpenIDGrantTypes;
import org.keycloak.cli.oidc.oidc.OpenIDParams;
import org.keycloak.cli.oidc.oidc.OpenIDScopes;
import org.keycloak.cli.oidc.oidc.exceptions.TokenRequestFailure;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.WellKnown;

import java.io.IOException;

public class ResourceOwnerFlow extends AbstractFlow {

    public ResourceOwnerFlow(Context configuration, WellKnown wellKnown) {
        super(configuration, wellKnown);
    }

    @Override
    public TokenResponse execute() throws TokenRequestFailure {
        try {
            return clientRequest(wellKnown.getTokenEndpoint())
                    .accept(MimeType.JSON)
                    .contentType(MimeType.FORM)
                    .body(OpenIDParams.GRANT_TYPE, OpenIDGrantTypes.PASSWORD)
                    .body(OpenIDParams.SCOPE, OpenIDScopes.OPENID)
                    .body(OpenIDParams.USERNAME, context.getUsername())
                    .body(OpenIDParams.PASSWORD, context.getUserPassword())
                    .asObject(TokenResponse.class);
        } catch (IOException e) {
            throw new TokenRequestFailure(e);
        }
    }
}
