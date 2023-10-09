package org.keycloak.cli.oidc.oidc.representations;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class StringOrArrayDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode n = p.readValueAsTree();
        if (n.isArray()) {
            ArrayList<String> a = new ArrayList<>();
            Iterator<JsonNode> itr = n.iterator();
            while (itr.hasNext()) {
                a.add(itr.next().textValue());
            }
            return a.toArray(new String[a.size()]);
        } else {
            return new String[]{ n.textValue() };
        }
    }

}
