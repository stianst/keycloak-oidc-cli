package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.keycloak.client.oauth.OpenIDFlow;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Context {

    private String name;
    private String issuer;
    private OpenIDFlow flow;

    @JsonProperty("client-id")
    private String clientId;
    @JsonProperty("client-secret")
    private String clientSecret;
    @JsonProperty("scope")
    private String scope;

    @JsonProperty("user")
    private String username;
    @JsonProperty("user-password")
    private String userPassword;

    @JsonProperty("store-tokens")
    private Boolean storeTokens;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        if (issuer != null && issuer.endsWith("/")) {
            issuer = issuer.substring(0, issuer.length() - 1);
        }
        this.issuer = issuer;
    }

    public OpenIDFlow getFlow() {
        return flow;
    }

    public void setFlow(OpenIDFlow flow) {
        this.flow = flow;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Boolean isStoreTokens() {
        return storeTokens;
    }

    public void setStoreTokens(Boolean storeTokens) {
        this.storeTokens = storeTokens;
    }

}
