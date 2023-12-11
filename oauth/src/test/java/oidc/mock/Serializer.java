package oidc.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Serializer {

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Serializer instance = new Serializer();

    public static Serializer get() {
        return instance;
    }

    public byte[] toBytes(Object o) throws IOException {
        try {
            return objectMapper.writeValueAsString(o).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }

}
