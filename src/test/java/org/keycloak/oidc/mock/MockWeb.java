package org.keycloak.oidc.mock;

import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.http.client.Http;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MockWeb implements User.Web {

    private Queue<URI> uris;

    public MockWeb() {
        uris = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean isDesktopSupported() {
        return true;
    }

    @Override
    public void browse(URI uri) {
        uris.add(uri);
        new Thread(() -> {
            try {
                Http.create(uri.toString()).send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public URI poll() {
        return uris.poll();
    }

}
