package org.keycloak.client.oauth;

import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenCacheException;
import org.keycloak.client.oauth.exceptions.TokenManagerException;
import org.keycloak.client.oauth.representations.TokenResponse;
import org.keycloak.client.oauth.representations.jwt.JwtClaims;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TokenManager {

    private OpenIDRequest openIDRequest;
    private TokenCache tokenCache;

    private OpenIDClient client;

    public TokenManager(OpenIDRequest openIDRequest, TokenCache tokenCache, OpenIDClient client) {
        this.openIDRequest = openIDRequest;
        this.tokenCache = tokenCache;
        this.client = client;
    }

    public OpenIDClient getClient() {
        return client;
    }

    public String getToken(TokenType tokenType, boolean forceRefresh, boolean offline) throws OpenIDException, TokenCacheException, TokenManagerException {
        return getToken(tokenType, null, forceRefresh, offline);
    }

    public String getToken(TokenType tokenType, String scope, boolean forceRefresh, boolean offline) throws OpenIDException, TokenCacheException, TokenManagerException {
        String savedToken = getSaved(tokenType);

        if (!forceRefresh && isValid(savedToken, tokenType, scope)) {
            return savedToken;
        } else if (offline) {
            if (savedToken == null) {
                throw new TokenManagerException("No cached token");
            } else {
                throw new TokenManagerException("Token expired");
            }
        }

        TokenResponse tokenResponse = null;

        boolean refreshOnly = false;

        String refreshToken = getSaved(TokenType.REFRESH);
        if (isValid(refreshToken, TokenType.REFRESH, scope)) {
            try {
                tokenResponse = client.refreshRequest(refreshToken, scope);
                refreshOnly = true;
            } catch (OpenIDException e) {
            }
        }

        if (tokenResponse == null) {
            tokenResponse = client.tokenRequest(scope);
        }

        if (openIDRequest.isStoreTokens() == null || openIDRequest.isStoreTokens()) {
            String refreshScope = tokenCache.getRefreshScope() != null ? tokenCache.getRefreshScope() : scope;
            tokenCache.update(
                openIDRequest.getIssuer(),
                openIDRequest.getClientId(),
                tokenResponse.getRefreshToken(),
                tokenResponse.getAccessToken(),
                tokenResponse.getIdToken(),
                refreshScope,
                scope
            );
        }

        return getToken(tokenResponse, tokenType);
    }

    private boolean isValid(String token, TokenType tokenType, String requestedScope) {
        if (token == null) {
            return false;
        }

        if (!Objects.equals(tokenCache.getIssuer(), openIDRequest.getIssuer())) {
            return false;
        }
        if (!Objects.equals(tokenCache.getClientId(), openIDRequest.getClientId())) {
            return false;
        }
        if (tokenType.equals(TokenType.REFRESH)) {
            if (!compatibleScopes(tokenCache.getRefreshToken(), requestedScope)) {
                return false;
            }
        } else if (requestedScope != null) {
            if (!Objects.equals(tokenCache.getTokenScope(), requestedScope)) {
                return false;
            }
        }

        JwtClaims claims = TokenParser.parse(token).getClaims();
        long exp = TimeUnit.SECONDS.toMillis(Long.valueOf(claims.getExp()));
        return exp > System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
    }

    public String getSaved(TokenType tokenType) {
        switch (tokenType) {
            case ID:
                return tokenCache.getIdToken();
            case ACCESS:
                return tokenCache.getAccessToken();
            case REFRESH:
                return tokenCache.getRefreshToken();
        }
        throw new RuntimeException("Unknown token type");
    }

    private String getToken(TokenResponse tokenResponse, TokenType tokenType) {
        switch (tokenType) {
            case ID:
                return tokenResponse.getIdToken();
            case ACCESS:
                return tokenResponse.getAccessToken();
            case REFRESH:
                return tokenResponse.getRefreshToken();
        }
        throw new RuntimeException("Unknown token type");
    }

    private boolean compatibleScopes(String originalScopeString, String requestedScopeString) {
        if (requestedScopeString == null || requestedScopeString.isEmpty()) {
            return true;
        }
        Set<String> originalScopes = new HashSet<>();
        for (String o : originalScopeString.split(" ")) {
            originalScopes.add(o);
        }

        for (String r : requestedScopeString.split(" ")) {
            if (!originalScopes.contains(r)) {
                return false;
            }
        }

        return true;
    }

}
