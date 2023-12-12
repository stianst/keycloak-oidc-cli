package org.keycloak.kauth.oauth.representations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StringOrArraySerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String[] a = (String[]) value;
        if (a == null) {
            gen.writeNull();
        } else if (a.length == 1) {
            gen.writeString(a[0]);
        } else {
            gen.writeStartArray();
            for (String s : a) {
                gen.writeString(s);
            }
            gen.writeEndArray();
        }
    }

}
