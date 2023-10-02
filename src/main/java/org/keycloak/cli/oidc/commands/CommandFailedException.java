package org.keycloak.cli.oidc.commands;

public class CommandFailedException extends RuntimeException {

    public CommandFailedException(Throwable cause) {
        super(cause);
    }

}
