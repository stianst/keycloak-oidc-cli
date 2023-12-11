package oidc.mock;

import org.keycloak.client.http.Http;
import org.keycloak.client.oauth.User;

import java.net.URI;
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
