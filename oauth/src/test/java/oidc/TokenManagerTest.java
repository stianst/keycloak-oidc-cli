package oidc;

import com.fasterxml.jackson.databind.ObjectMapper;
import oidc.mock.FakeJwt;
import oidc.mock.OpenIDTestProviderExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.client.oauth.OpenIDClient;
import org.keycloak.client.oauth.OpenIDFlow;
import org.keycloak.client.oauth.OpenIDRequest;
import org.keycloak.client.oauth.TokenCache;
import org.keycloak.client.oauth.TokenManager;
import org.keycloak.client.oauth.TokenType;
import org.keycloak.client.oauth.exceptions.OpenIDException;
import org.keycloak.client.oauth.exceptions.TokenCacheException;
import org.keycloak.client.oauth.exceptions.TokenManagerException;
import org.keycloak.client.oauth.representations.TokenResponse;
import org.keycloak.client.oauth.representations.jwt.JwtClaims;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

@ExtendWith(OpenIDTestProviderExtension.class)
public class TokenManagerTest {

    private FakeJwt fakeJwt = new FakeJwt("https://myissuer", new ObjectMapper());
    private TokenManager tokenManager;
    private OpenIDRequest context;
    private OpenIDClient client;
    private TokenCache tokenCache;

    @BeforeEach
    public void before() {
        context = OpenIDTestUtils.createContext("https://myissuer", OpenIDFlow.RESOURCE_OWNER);
        client = Mockito.mock(OpenIDClient.class);
        tokenCache = Mockito.mock(TokenCache.class);
        tokenManager = new TokenManager(context, tokenCache, client);
    }

    @Test
    public void testNoSavedOffline() {
        TokenManagerException exception = Assertions.assertThrows(TokenManagerException.class, () -> tokenManager.getToken(TokenType.ACCESS, false, true));
        Assertions.assertEquals("No cached token", exception.getMessage());

        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testSavedOffline() throws OpenIDException, TokenManagerException, TokenCacheException {
        String refreshToken = createToken(TokenType.REFRESH, false);
        String accessToken = createToken(TokenType.ACCESS, false);
        String idToken = createToken(TokenType.ID, false);

//        tokenCache.setAccessToken(accessToken);
//        tokenCache.setRefreshToken(refreshToken);
//        tokenCache.setIdToken(idToken);

        Assertions.assertEquals(refreshToken, tokenManager.getToken(TokenType.REFRESH, false, true));
        Assertions.assertEquals(accessToken, tokenManager.getToken(TokenType.ACCESS, false, true));
        Assertions.assertEquals(idToken, tokenManager.getToken(TokenType.ID, false, true));

        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testTokenRequest() throws OpenIDException, TokenManagerException, TokenCacheException {
        TokenResponse tokenResponse = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));
        Assertions.assertEquals(tokenResponse.getRefreshToken(), tokenManager.getToken(TokenType.REFRESH, false, false));
        Assertions.assertEquals(tokenResponse.getIdToken(), tokenManager.getToken(TokenType.ID, false, false));

        Mockito.verify(client, Mockito.times(3)).tokenRequest();
//        Mockito.verify(tokenCache, Mockito.never()).save();
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testTokenRequestCacheTokens() throws OpenIDException, TokenManagerException, TokenCacheException {
        context.setStoreTokens(true);
        TokenResponse tokenResponse = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));

        Assertions.assertEquals(tokenResponse.getRefreshToken(), tokenCache.getRefreshToken());
        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenCache.getAccessToken());
        Assertions.assertEquals(tokenResponse.getIdToken(), tokenCache.getIdToken());

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));

        Mockito.verify(client, Mockito.times(1)).tokenRequest();
//        Mockito.verify(tokenCache, Mockito.times(1)).save();
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
//        tokenCacheContext.setRefreshToken(createToken(TokenType.REFRESH, false));
//        tokenCacheContext.setAccessToken(createToken(TokenType.ACCESS, true));
//        tokenCacheContext.setIdToken(createToken(TokenType.ID, false));

        TokenResponse response = createTokenResponse();
        Mockito.when(client.refreshRequest(tokenCache.getRefreshToken())).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(tokenCache.getRefreshToken());
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testRefreshExpiredRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
//        tokenCacheContext.setRefreshToken(createToken(TokenType.REFRESH, true));
//        tokenCacheContext.setAccessToken(createToken(TokenType.ACCESS, true));
//        tokenCacheContext.setIdToken(createToken(TokenType.ID, true));

        TokenResponse response = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).tokenRequest();
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testCached() throws OpenIDException, TokenManagerException, TokenCacheException {
//        tokenCacheContext.setRefreshToken(createToken(TokenType.REFRESH, false));
//        tokenCacheContext.setAccessToken(createToken(TokenType.ACCESS, false));
//        tokenCacheContext.setIdToken(createToken(TokenType.ID, false));

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(tokenCache.getAccessToken(), token);

        String token2 = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(token, token2);

        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testCachedForceRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
//        tokenCacheContext.setRefreshToken(createToken(TokenType.REFRESH, false));
//        tokenCacheContext.setAccessToken(createToken(TokenType.ACCESS, false));
//        tokenCacheContext.setIdToken(createToken(TokenType.ID, false));

        TokenResponse response = createTokenResponse();
        Mockito.when(client.refreshRequest(tokenCache.getRefreshToken())).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, true, false);

        Assertions.assertNotEquals(tokenCache.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(tokenCache.getRefreshToken());
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    @Test
    public void testCachedForceRefreshWithInvalidRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
//        tokenCacheContext.setRefreshToken(createToken(TokenType.REFRESH, false));
//        tokenCacheContext.setAccessToken(createToken(TokenType.ACCESS, false));
//        tokenCacheContext.setIdToken(createToken(TokenType.ID, false));

        TokenResponse response = new TokenResponse();
        response.setError("invalid");

        Mockito.when(client.refreshRequest(tokenCache.getRefreshToken())).thenThrow(new OpenIDException("Invalid token"));

        response = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, true, false);

        Assertions.assertNotEquals(tokenCache.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(tokenCache.getRefreshToken());
        Mockito.verify(client, Mockito.times(1)).tokenRequest();
        Mockito.verifyNoMoreInteractions(client, tokenCache);
    }

    private TokenResponse createTokenResponse() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken(createToken(TokenType.REFRESH, false));
        tokenResponse.setAccessToken(createToken(TokenType.ACCESS, false));
        tokenResponse.setIdToken(createToken(TokenType.ID, false));
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

}