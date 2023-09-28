package org.keycloak.oidc.mock;

import org.keycloak.cli.oidc.http.server.HttpRequest;

import java.io.IOException;

public interface Request {

    String getExpectedPath();

    void processRequest(HttpRequest httpRequest) throws IOException;

}
