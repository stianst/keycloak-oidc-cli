package org.keycloak.cli.oidc.oidc;

import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.TokenCacheContext;
import org.keycloak.cli.oidc.config.TokenCacheException;
import org.keycloak.cli.oidc.config.TokenCacheHandler;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenManagerException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;

import java.util.Objects;
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
        String savedToken = getSaved(tokenType);

        if (!forceRefresh && isValid(savedToken)) {
            return savedToken;
        } else if (offline) {
            if (savedToken == null) {
                throw new TokenManagerException("No cached token");
            } else {
                throw new TokenManagerException("Token expired");
            }
        }

        TokenResponse tokenResponse = null;

        String refreshToken = getSaved(TokenType.REFRESH);
        if (isValid(refreshToken)) {
            try {
                tokenResponse = client.refreshRequest(refreshToken);
            } catch (OpenIDException e) {
            }
        }

        if (tokenResponse == null) {
            tokenResponse = client.tokenRequest();
        }

        if (context.isStoreTokens() == null || context.isStoreTokens()) {
            tokenCacheContext.setClientId(context.getClientId());
            tokenCacheContext.setScope(context.getScope());
            tokenCacheContext.setRefreshToken(tokenResponse.getRefreshToken());
            tokenCacheContext.setIdToken(tokenResponse.getIdToken());
            tokenCacheContext.setAccessToken(tokenResponse.getAccessToken());
            tokenCacheHandler.save();
        }

        return getToken(tokenResponse, tokenType);
    }

    private boolean isValid(String token) {
        if (token == null) {
            return false;
        }
        JwtClaims claims = TokenParser.parse(token).getClaims();
        long exp = TimeUnit.SECONDS.toMillis(Long.valueOf(claims.getExp()));
        return exp > System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
    }

    public String getSaved(TokenType tokenType) {
        System.out.println("stored client: " + tokenCacheContext.getClientId() + ", requested " + context.getClientId());
        if (!Objects.equals(tokenCacheContext.getClientId(), context.getClientId())) {
            System.out.println("stored client differs ignoring");
            return null;
        }
        System.out.println("stored scope: " + tokenCacheContext.getScope() + ", requested " + context.getScope());
        if (!Objects.equals(tokenCacheContext.getScope(), context.getScope())) {
            System.out.println("stored scope differs ignoring");
            return null;
        }

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

}
