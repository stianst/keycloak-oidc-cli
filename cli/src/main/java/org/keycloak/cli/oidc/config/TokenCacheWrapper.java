package org.keycloak.cli.oidc.config;

import org.keycloak.kauth.oauth.TokenCache;
import org.keycloak.kauth.oauth.exceptions.TokenCacheException;

public class TokenCacheWrapper implements TokenCache {

    private final YamlTokenCacheHandler tokenCacheHandler;
    private final YamlTokenCacheContext tokenCacheContext;

    public TokenCacheWrapper(YamlTokenCacheHandler tokenCacheHandler, YamlTokenCacheContext tokenCacheContext) {
        this.tokenCacheHandler = tokenCacheHandler;
        this.tokenCacheContext = tokenCacheContext;
    }

    @Override
    public String getIssuer() {
        return tokenCacheContext.getIssuer();
    }

    @Override
    public String getClientId() {
        return tokenCacheContext.getClientId();
    }

    @Override
    public String getRefreshToken() {
        return tokenCacheContext.getRefreshToken();
    }

    @Override
    public String getRefreshScope() {
        return tokenCacheContext.getRefreshScope();
    }

    @Override
    public String getIdToken() {
        return tokenCacheContext.getIdToken();
    }

    @Override
    public String getAccessToken() {
        return tokenCacheContext.getAccessToken();
    }

    @Override
    public String getTokenScope() {
        return tokenCacheContext.getTokenScope();
    }

    @Override
    public void update(String issuer, String clientId, String refreshToken, String accessToken, String idToken, String refreshScope, String tokenScope) throws TokenCacheException {
        tokenCacheHandler.save();
    }
}
