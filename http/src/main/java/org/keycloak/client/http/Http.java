package org.keycloak.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Http implements AutoCloseable {

    private String endpoint;
    private HttpURLConnection connection;
    private String userAgent = "kc-http/1.0";
    private String authorization;
    private MimeType accept;
    private MimeType contentType;
    private Map<String, String> bodyParams = new HashMap<>();

    private Http(String endpoint) {
        this.endpoint = endpoint;
    }

    public static Http create(String endpoint) {
        return new Http(endpoint);
    }

    public Http accept(MimeType type) {
        this.accept = type;
        return this;
    }

    public Http contentType(MimeType type) {
        this.contentType = type;
        return this;
    }

    public Http body(String key, String value) {
        if (value != null && !value.isEmpty()) {
            bodyParams.put(key, value);
        }
        return this;
    }

    public Http userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Http authorization(String token) {
        authorization = "Bearer " + token;
        return this;
    }

    public Http authorization(String username, String password) {
        authorization = "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        return this;
    }

    public void send() throws IOException {
        connect();
    }

    public <T> T asObject(Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(connect(), clazz);
    }

    private InputStream connect() throws IOException {
        createConnection();
        connection.setRequestProperty(HttpHeaders.USER_AGENT, userAgent);
        if (authorization != null) {
            connection.setRequestProperty(HttpHeaders.AUTHORIZATION, authorization);
        }
        sendBodyIfAvailable();
        return connection.getInputStream();
    }

    private void createConnection() throws IOException {
        connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setReadTimeout(10000);
        if (accept != null) {
            connection.setRequestProperty(HttpHeaders.ACCEPT, accept.toString());
        }
    }

    private void sendBodyIfAvailable() throws IOException {
        if (contentType == null) {
            return;
        }

        if (contentType.equals(MimeType.FORM)) {
            connection.setDoOutput(true);
            connection.setRequestMethod(HttpMethods.POST);
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType.toString());

            byte[] body = encodeParams(bodyParams).getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length));
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(body);
            }
            connection.getOutputStream().close();
        }
    }

    private String encodeParams(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        if (!map.isEmpty()) {
            Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> q = itr.next();
                sb.append(URLEncoder.encode(q.getKey(), StandardCharsets.UTF_8));
                sb.append("=");
                sb.append(URLEncoder.encode(q.getValue(), StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void close() {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
