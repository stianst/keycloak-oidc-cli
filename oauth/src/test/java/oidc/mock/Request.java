package oidc.mock;

import org.keycloak.kauth.http.server.HttpRequest;

import java.io.IOException;

public interface Request {

    String getExpectedPath();

    void processRequest(HttpRequest httpRequest) throws IOException;

}
