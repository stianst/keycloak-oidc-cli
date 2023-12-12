package org.keycloak.kauth.oauth.exceptions;

public class OpenIDException extends Exception {
    public OpenIDException(String message) {
        super(message);
    }

    public OpenIDException(String message, Throwable cause) {
        super(message, cause);
    }

}
