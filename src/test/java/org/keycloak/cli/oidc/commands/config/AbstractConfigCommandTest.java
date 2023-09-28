package org.keycloak.cli.oidc.commands.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.keycloak.cli.oidc.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public abstract class AbstractConfigCommandTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    public void before() throws IOException {
        URL resource = ConfigCurrentCommandTest.class.getResource("sample-config.yaml");
        File file = tempDir.resolve("sample-config.yaml").toFile();
        FileOutputStream os = new FileOutputStream(file);
        os.write(resource.openStream().readAllBytes());
        os.close();

        System.setProperty(Constants.SYSPROP_CONF_FILE_KEY, file.getAbsolutePath());
        System.out.println(System.getProperty(Constants.SYSPROP_CONF_FILE_KEY));
    }

    @AfterEach
    public void after() {
        System.getProperties().remove(Constants.SYSPROP_CONF_FILE_KEY);
    }

}
