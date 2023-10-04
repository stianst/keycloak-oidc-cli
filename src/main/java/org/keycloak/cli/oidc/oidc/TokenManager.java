package org.keycloak.cli.oidc.oidc;

import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenManagerException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;

import java.util.concurrent.TimeUnit;

public class TokenManager {

    private Context context;
    private ConfigHandler configHandler;

    private OpenIDClient client;

    public TokenManager(Context context, ConfigHandler configHandler, OpenIDClient client) {
        this.context = context;
        this.configHandler = configHandler;
        this.client = client;
    }

    public OpenIDClient getClient() {
        return client;
    }

    public String getToken(TokenType tokenType, boolean forceRefresh, boolean offline) throws OpenIDException, ConfigException, TokenManagerException {
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

        String refreshToken = context.getRefreshToken();
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
            context.setRefreshToken(tokenResponse.getRefreshToken());
            context.setIdToken(tokenResponse.getIdToken());
            context.setAccessToken(tokenResponse.getAccessToken());
            configHandler.save();
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
        switch (tokenType) {
            case ID:
                return context.getIdToken();
            case ACCESS:
                return context.getAccessToken();
            case REFRESH:
                return context.getRefreshToken();
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
