package cli.oidc.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import cli.oidc.commands.config.ConfigCurrentCommandTest;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Environment;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandlerExtension implements BeforeEachCallback, AfterEachCallback {

    private Path tmpConfigFile;
    private Path tmpTokenFile;

    @Override
    public void afterEach(ExtensionContext extensionContext) throws IOException {
        System.getProperties().remove(Environment.SYSPROP_CONF_FILE_KEY);
        System.getProperties().remove(Environment.SYSPROP_TOKEN_CACHE_FILE_KEY);
        ConfigHandler.clearInstance();
        if (tmpConfigFile.toFile().exists()) {
            Files.delete(tmpConfigFile);
        }
        if (tmpTokenFile.toFile().exists()) {
            Files.delete(tmpTokenFile);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        tmpConfigFile = Files.createTempFile("kc-oidc-test-config-", ".yaml");
        tmpTokenFile = Files.createTempFile("kc-oidc-test-tokens-", ".yaml");

        URL resource = ConfigCurrentCommandTest.class.getResource("sample-config.yaml");

        FileOutputStream os = new FileOutputStream(tmpConfigFile.toFile());
        os.write(resource.openStream().readAllBytes());
        os.close();

        System.setProperty(Environment.SYSPROP_CONF_FILE_KEY, tmpConfigFile.toFile().getAbsolutePath());
        System.setProperty(Environment.SYSPROP_TOKEN_CACHE_FILE_KEY, tmpTokenFile.toFile().getAbsolutePath());

        ConfigHandler.clearInstance();
        YamlTokenCacheHandler.clearInstance();
    }
}
