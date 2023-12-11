package org.keycloak.client.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OpenIDFlow {

    @JsonProperty("authorization-code")
    AUTHORIZATION_CODE,
    @JsonProperty("client-credential")
    CLIENT_CREDENTIAL,
    @JsonProperty("device")
    DEVICE,
    @JsonProperty("resource-owner")
    RESOURCE_OWNER

}
