package org.keycloak.oidc.mock;

import org.keycloak.cli.oidc.http.server.BasicWebServer;
import org.keycloak.cli.oidc.http.server.HttpRequest;

import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RequestHandler extends Thread {

    private BasicWebServer webServer;
    private FakeJwt fakeJwt;
    private String issuerUrl;
    private Queue<Request> expectedRequests;
    private Queue<HttpRequest> actualRequests;

    public RequestHandler(BasicWebServer webServer, FakeJwt fakeJwt, String issuerUrl) {
        this.webServer = webServer;
        this.fakeJwt = fakeJwt;
        this.issuerUrl = issuerUrl;
        this.actualRequests = new ConcurrentLinkedDeque<>();
        this.expectedRequests = new ConcurrentLinkedDeque<>();
    }

    public void expectWellKnown() {
        expectedRequests.add(new WellKnownRequest(issuerUrl));
    }

    public void expectTokenRequest() {
        expectedRequests.add(new TokenRequest(fakeJwt));
    }

    public void expectTokenRequestFailure(String returnError) {
        expectedRequests.add(new TokenErrorRequest(returnError));
    }

    public void expectDeviceAuthz() {
        expectedRequests.add(new DeviceAuthzRequest(issuerUrl));
    }

    public void expectAuthzRequest() {
        expectedRequests.add(new AuthzRequest());
    }

    public void expectIntrospectionRequest() {
        expectedRequests.add(new TokenIntrospectionRequest(issuerUrl));
    }

    public void expectUserInfoRequest() {
        expectedRequests.add(new UserInfoRequest(issuerUrl));
    }

    public HttpRequest pollRequest() {
        return actualRequests.poll();
    }

    @Override
    public void run() {
        try {
            while (true) {
                HttpRequest httpRequest = webServer.accept();
                actualRequests.add(httpRequest);

                Request expected = expectedRequests.poll();
                if (expected == null) {
                    System.err.println("Unexpected request to: " + httpRequest.getPath());
                } else if (!expected.getExpectedPath().equals(httpRequest.getPath())) {
                    System.err.println("Unexpected request to: " + httpRequest.getPath() + ", expected: " + expected.getExpectedPath());
                    httpRequest.serverError();
                } else {
                    expected.processRequest(httpRequest);
                }
            }
        } catch (SocketException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        expectedRequests.clear();
        actualRequests.clear();
    }

}
