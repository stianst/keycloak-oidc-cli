package org.keycloak.cli.oidc.oidc.exceptions;

public class TokenRequestFailure extends OpenIDException {

    public TokenRequestFailure(Exception exception) {
        super("Token request failed", exception);
    }

}
