package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class YamlTokenCacheContext {

    private String context;

    @JsonProperty("iss")
    private String issuer;

    @JsonProperty("client-id")
    private String clientId;

    @JsonProperty("refresh-scope")
    private String refreshScope;

    @JsonProperty("token-scope")
    private String tokenScope;

    @JsonProperty("refresh-token")
    private String refreshToken;

    @JsonProperty("access-token")
    private String accessToken;

    @JsonProperty("id-token")
    private String idToken;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshScope() {
        return refreshScope;
    }

    public void setRefreshScope(String refreshScope) {
        this.refreshScope = refreshScope;
    }

    public String getTokenScope() {
        return tokenScope;
    }

    public void setTokenScope(String tokenScope) {
        this.tokenScope = tokenScope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
