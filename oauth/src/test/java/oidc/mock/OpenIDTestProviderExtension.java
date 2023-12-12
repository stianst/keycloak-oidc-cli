package oidc.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.keycloak.kauth.http.server.BasicWebServer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class OpenIDTestProviderExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        requestHandler.clear();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface IssuerUrl {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Requests {
    }

    private BasicWebServer webServer;
    private RequestHandler requestHandler;
    private String issuerUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    private FakeJwt fakeJwt;

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (webServer != null) {
            webServer.stop();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        webServer = BasicWebServer.start();
        issuerUrl = "http://127.0.0.1:" + webServer.getPort();
        fakeJwt = new FakeJwt(issuerUrl, objectMapper);
        requestHandler = new RequestHandler(webServer, fakeJwt, issuerUrl);
        requestHandler.start();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(IssuerUrl.class) || parameterContext.isAnnotated(Requests.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.isAnnotated(IssuerUrl.class)) {
            return issuerUrl;
        } else if (parameterContext.isAnnotated(Requests.class)) {
            return requestHandler;
        }
        return null;
    }

}
