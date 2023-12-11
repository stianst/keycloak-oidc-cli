package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;

public class YamlFileStore<T> {

    private File file;
    private ObjectMapper objectMapper;
    private Class<T> dataClazz;

    public YamlFileStore(File file, Class<T> dataClazz) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.file = file;
        this.dataClazz = dataClazz;
    }

    public T reload() throws IOException {
        if (file.isFile() && new FileInputStream(file).read() != -1) {
            return objectMapper.readValue(file, dataClazz);
        }
        return null;
    }

    public void save(T data) throws IOException {
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }

        boolean fileExists = file.exists();

        objectMapper.writeValue(file, data);

        if (!fileExists) {
            try {
                Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString("rw-------"));
            } catch (UnsupportedOperationException e) {
                file.setReadable(true, true);
                file.setWritable(true, true);
            }
        }
    }

    public void delete() {
        file.delete();
    }

}
