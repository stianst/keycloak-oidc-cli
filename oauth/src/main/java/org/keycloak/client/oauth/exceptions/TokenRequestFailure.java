package org.keycloak.client.oauth.exceptions;

public class TokenRequestFailure extends OpenIDException {

    public TokenRequestFailure(Exception exception) {
        super("Token request failed", exception);
    }

}
