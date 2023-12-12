package org.keycloak.kauth.oauth.exceptions;

public class TokenCacheException extends Exception {

    public TokenCacheException(String message) {
        super(message);
    }

    public TokenCacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
