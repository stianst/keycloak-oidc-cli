package org.keycloak.cli.oidc.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
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
