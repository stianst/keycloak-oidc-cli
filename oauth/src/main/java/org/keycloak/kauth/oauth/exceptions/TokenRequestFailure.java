package org.keycloak.kauth.oauth.exceptions;

public class TokenRequestFailure extends OpenIDException {

    public TokenRequestFailure(Exception exception) {
        super("Token request failed", exception);
    }

}
