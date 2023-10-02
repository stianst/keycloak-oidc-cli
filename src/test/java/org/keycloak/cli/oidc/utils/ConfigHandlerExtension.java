package org.keycloak.cli.oidc.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.keycloak.cli.oidc.commands.config.ConfigCurrentCommandTest;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandlerExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private Path tmpConfigFile;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(Config.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.isAnnotated(Config.class)) {
            try {
                return ConfigHandler.get();
            } catch (ConfigException e) {
                throw new ParameterResolutionException("Failed to retrieve config", e);
            }
        }
        return null;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Config {
    }


    @Override
    public void afterEach(ExtensionContext extensionContext) throws IOException {
        System.getProperties().remove(Environment.SYSPROP_CONF_FILE_KEY);
        ConfigHandler.clearInstance();
        Files.delete(tmpConfigFile);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        tmpConfigFile = Files.createTempFile("kc-oidc-test", ".yaml");
        URL resource = ConfigCurrentCommandTest.class.getResource("sample-config.yaml");
        File file = tmpConfigFile.toFile();

        FileOutputStream os = new FileOutputStream(file);
        os.write(resource.openStream().readAllBytes());
        os.close();

        System.setProperty(Environment.SYSPROP_CONF_FILE_KEY, file.getAbsolutePath());
    }
}
