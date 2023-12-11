package org.keycloak.client.oauth.exceptions;

public class OpenIDException extends Exception {
    public OpenIDException(String message) {
        super(message);
    }

    public OpenIDException(String message, Throwable cause) {
        super(message, cause);
    }

}
