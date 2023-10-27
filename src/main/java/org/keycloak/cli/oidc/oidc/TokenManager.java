package org.keycloak.cli.oidc.oidc;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheContext;
import org.keycloak.cli.oidc.config.TokenCacheException;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenManagerException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TokenManager {

    private Context context;
    private TokenCacheHandler tokenCacheHandler;
    private TokenCacheContext tokenCacheContext;

    private OpenIDClient client;

    public TokenManager(Context context, TokenCacheHandler tokenCacheHandler, OpenIDClient client) {
        this.context = context;
        this.tokenCacheHandler = tokenCacheHandler;
        this.tokenCacheContext = tokenCacheHandler.getTokenCacheContext(context);
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

        if (context.isStoreTokens() == null || context.isStoreTokens()) {
            tokenCacheContext.setIssuer(context.getIssuer());
            tokenCacheContext.setClientId(context.getClientId());
            if (!refreshOnly) {
                tokenCacheContext.setRefreshScope(scope);
            }
            tokenCacheContext.setTokenScope(scope);
            tokenCacheContext.setRefreshToken(tokenResponse.getRefreshToken());
            tokenCacheContext.setIdToken(tokenResponse.getIdToken());
            tokenCacheContext.setAccessToken(tokenResponse.getAccessToken());
            tokenCacheHandler.save();
        }

        return getToken(tokenResponse, tokenType);
    }

    private boolean isValid(String token, TokenType tokenType, String requestedScope) {
        if (token == null) {
            return false;
        }

        if (!Objects.equals(tokenCacheContext.getIssuer(), context.getIssuer())) {
            return false;
        }
        if (!Objects.equals(tokenCacheContext.getClientId(), context.getClientId())) {
            return false;
        }
        if (tokenType.equals(TokenType.REFRESH)) {
            if (!compatibleScopes(tokenCacheContext.getRefreshToken(), requestedScope)) {
                return false;
            }
        } else if (requestedScope != null) {
            if (!Objects.equals(tokenCacheContext.getTokenScope(), requestedScope)) {
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
                return tokenCacheContext.getIdToken();
            case ACCESS:
                return tokenCacheContext.getAccessToken();
            case REFRESH:
                return tokenCacheContext.getRefreshToken();
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
