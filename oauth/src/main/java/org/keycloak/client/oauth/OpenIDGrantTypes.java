package org.keycloak.client.oauth;

public interface OpenIDGrantTypes {

    String PASSWORD = "password";
    String REFRESH_TOKEN = "refresh_token";

    String AUTHORIZATION_CODE = "authorization_code";

    String CLIENT_CREDENTIAL = "client_credentials";

    String DEVICE_CODE = "urn:ietf:params:oauth:grant-type:device_code";

}
