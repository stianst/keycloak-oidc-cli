package org.keycloak.cli.oidc.commands;

import picocli.CommandLine;

public class CommandExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    private boolean verbose;

    public CommandExceptionHandler() {
        verbose = System.getenv().containsKey("KC_VERBOSE");
    }

    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        if (verbose) {
            e.printStackTrace(commandLine.getErr());
        } else {
            Throwable t = e;
            if (e instanceof CommandFailedException) {
                t = e.getCause();
            }

            StringBuilder error = new StringBuilder();
            if (t.getMessage() != null) {
                error.append(t.getMessage());
            } else {
                error.append(t.getClass().getName());
            }
            if (t.getCause() != null) {
                error.append(": ");
                if (t.getCause().getMessage() != null) {
                    error.append(t.getCause().getMessage());
                } else {
                    error.append(t.getCause().getClass().getName());
                }
            }
            commandLine.getErr().println(error);
        }
        return 1;
    }
}
