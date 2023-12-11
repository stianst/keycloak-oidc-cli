package org.keycloak.client.oauth;

public class OpenIDRequest {

    private String issuer;
    private OpenIDFlow flow;

    private String clientId;
    private String clientSecret;
    private String scope;

    private String username;
    private String userPassword;

    private Boolean storeTokens;

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
