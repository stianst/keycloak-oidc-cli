package org.keycloak.kauth.oauth;

public class OAuthRequest {

    private String issuer;
    private OAuthFlow flow;

    private String clientId;
    private String clientSecret;
    private String scope;

    private String username;
    private String userPassword;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        if (issuer != null && issuer.endsWith("/")) {
            issuer = issuer.substring(0, issuer.length() - 1);
        }
        this.issuer = issuer;
    }

    public OAuthFlow getFlow() {
        return flow;
    }

    public void setFlow(OAuthFlow flow) {
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

}
