package org.keycloak.kauth.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OAuthFlow {

    @JsonProperty("authorization-code")
    AUTHORIZATION_CODE,
    @JsonProperty("client-credential")
    CLIENT_CREDENTIAL,
    @JsonProperty("device")
    DEVICE,
    @JsonProperty("resource-owner")
    RESOURCE_OWNER

}
