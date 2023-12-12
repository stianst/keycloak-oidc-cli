package oidc.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.keycloak.kauth.oauth.TokenCache;
import org.keycloak.kauth.oauth.TokenType;
import org.keycloak.kauth.oauth.exceptions.TokenCacheException;
import org.keycloak.kauth.oauth.representations.TokenResponse;
import org.keycloak.kauth.oauth.representations.jwt.JwtClaims;

import java.util.concurrent.TimeUnit;

public class TokenHolder {

    private FakeJwt fakeJwt;
    private String issuer;
    private String clientId;
    private String refreshToken;
    private String accessToken;
    private String idToken;
    private String refreshScope;
    private String tokenScope;
    private MockTokenCache cache;

    public static TokenHolder create(String issuer, String clientId) {
        return new TokenHolder(issuer, clientId);
    }

    public TokenHolder(String issuer, String clientId) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.fakeJwt = new FakeJwt("https://myissuer", new ObjectMapper());
        this.cache = new MockTokenCache();
    }

    public TokenHolder initTokens() {
        refreshToken = createToken(TokenType.REFRESH, false);
        accessToken = createToken(TokenType.ACCESS, false);
        idToken = createToken(TokenType.ID, false);
        return initTokens(false, false);
    }

    public TokenHolder initTokens(boolean refreshExpired, boolean tokensExpired) {
        refreshToken = createToken(TokenType.REFRESH, refreshExpired);
        accessToken = createToken(TokenType.ACCESS, tokensExpired);
        idToken = createToken(TokenType.ID, tokensExpired);
        return this;
    }

    public TokenHolder initCache() {
        this.cache.issuer = issuer;
        this.cache.clientId = clientId;
        this.cache.refreshToken = refreshToken;
        this.cache.accessToken = accessToken;
        this.cache.idToken = idToken;
        this.cache.refreshScope = refreshScope;
        this.cache.tokenScope = tokenScope;
        return this;
    }

    public MockTokenCache getCache() {
        return cache;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRefreshScope() {
        return refreshScope;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenScope() {
        return tokenScope;
    }

    public TokenResponse createTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setIdToken(idToken);
        return tokenResponse;
    }

    private String createToken(TokenType tokenType, boolean expired) {
        JwtClaims claims = new JwtClaims();
        claims.setIss("https://myissuer");
        long currentTime = (System.currentTimeMillis() / 1000);
        if (expired) {
            currentTime = currentTime - TimeUnit.MINUTES.toSeconds(5);
        }
        claims.setIat(currentTime);
        claims.setExp(currentTime + TimeUnit.MINUTES.toSeconds(1));
        claims.getClaims().put("typ", tokenType.toString());
        return fakeJwt.create(tokenType, claims);
    }

    public class MockTokenCache implements TokenCache {

        private int updates = 0;
        private String issuer;
        private String clientId;
        private String refreshToken;
        private String accessToken;
        private String idToken;
        private String refreshScope;
        private String tokenScope;

        @Override
        public String getIssuer() {
            return issuer;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public String getRefreshToken() {
            return refreshToken;
        }

        @Override
        public String getRefreshScope() {
            return refreshScope;
        }

        @Override
        public String getIdToken() {
            return idToken;
        }

        @Override
        public String getAccessToken() {
            return accessToken;
        }

        @Override
        public String getTokenScope() {
            return tokenScope;
        }

        public boolean isUpdated() {
            return updates > 0;
        }

        public int getUpdates() {
            return updates;
        }

        @Override
        public void update(String issuer, String clientId, String refreshToken, String accessToken, String idToken, String refreshScope, String tokenScope) throws TokenCacheException {
            this.updates++;
            this.issuer = issuer;
            this.clientId = clientId;
            this.refreshToken = refreshToken;
            this.accessToken = accessToken;
            this.idToken = idToken;
            this.refreshScope = refreshScope;
            this.tokenScope = tokenScope;
        }

        public void assertUpdates() {
            Assertions.assertEquals(1, updates);
            Assertions.assertEquals(TokenHolder.this.issuer, issuer);
            Assertions.assertEquals(TokenHolder.this.clientId, clientId);
            Assertions.assertEquals(TokenHolder.this.refreshToken, refreshToken);
            Assertions.assertEquals(TokenHolder.this.accessToken, accessToken);
            Assertions.assertEquals(TokenHolder.this.idToken, idToken);
            Assertions.assertEquals(TokenHolder.this.refreshScope, refreshScope);
            Assertions.assertEquals(TokenHolder.this.tokenScope, tokenScope);
        }

    }

}
