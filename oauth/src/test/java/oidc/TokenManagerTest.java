package oidc;

import com.fasterxml.jackson.databind.ObjectMapper;
import oidc.mock.FakeJwt;
import oidc.mock.OpenIDTestProviderExtension;
import oidc.mock.TokenHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthFlow;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.TokenManager;
import org.keycloak.kauth.oauth.TokenType;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.exceptions.TokenCacheException;
import org.keycloak.kauth.oauth.exceptions.TokenManagerException;
import org.keycloak.kauth.oauth.representations.TokenResponse;
import org.mockito.Mockito;

@ExtendWith(OpenIDTestProviderExtension.class)
public class TokenManagerTest {

    private FakeJwt fakeJwt = new FakeJwt("https://myissuer", new ObjectMapper());
    private TokenManager tokenManager;
    private OAuthRequest context;
    private OAuthClient client;
    private TokenHolder tokenHolder;

    @BeforeEach
    public void before() {
        context = OpenIDTestUtils.createContext("https://myissuer", OAuthFlow.RESOURCE_OWNER);
        client = Mockito.mock(OAuthClient.class);
        tokenHolder = TokenHolder.create(context.getIssuer(), context.getClientId());
    }

    @Test
    public void testNoSavedOffline() {
        tokenManager = new TokenManager(context, null, client);

        TokenManagerException exception = Assertions.assertThrows(TokenManagerException.class, () -> tokenManager.getToken(TokenType.ACCESS, false, true));
        Assertions.assertEquals("No cached token", exception.getMessage());

        Mockito.verifyNoMoreInteractions(client);
        Assertions.assertFalse(tokenHolder.getCache().isUpdated());
    }

    @Test
    public void testSavedOffline() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens().initCache();

        Assertions.assertEquals(tokenHolder.getRefreshToken(), tokenManager.getToken(TokenType.REFRESH, false, true));
        Assertions.assertEquals(tokenHolder.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, true));
        Assertions.assertEquals(tokenHolder.getIdToken(), tokenManager.getToken(TokenType.ID, false, true));

        Mockito.verifyNoMoreInteractions(client);
        Assertions.assertFalse(tokenHolder.getCache().isUpdated());
    }

    @Test
    public void testTokenRequest() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, null, client);
        tokenHolder.initTokens();

        TokenResponse tokenResponse = tokenHolder.createTokenResponse();
        Mockito.when(client.tokenRequest(null)).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenHolder.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));
        Assertions.assertEquals(tokenHolder.getRefreshToken(), tokenManager.getToken(TokenType.REFRESH, false, false));
        Assertions.assertEquals(tokenHolder.getIdToken(), tokenManager.getToken(TokenType.ID, false, false));

        Mockito.verify(client, Mockito.times(3)).tokenRequest(null);
        Mockito.verifyNoMoreInteractions(client);
    }

    @Test
    public void testTokenRequestCacheTokens() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens();

        TokenResponse tokenResponse = tokenHolder.createTokenResponse();
        Mockito.when(client.tokenRequest(null)).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));

        tokenHolder.getCache().assertUpdates();

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false, false));

        Mockito.verify(client, Mockito.times(1)).tokenRequest(null);
        Mockito.verifyNoMoreInteractions(client);
        Assertions.assertEquals(1, tokenHolder.getCache().getUpdates());
    }

    @Test
    public void testRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens(false, true).initCache().initTokens();

        TokenResponse response = tokenHolder.createTokenResponse();
        String cachedRefreshToken = tokenHolder.getCache().getRefreshToken();
        Mockito.when(client.refreshRequest(cachedRefreshToken, null)).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(cachedRefreshToken, null);
        Mockito.verifyNoMoreInteractions(client);
        tokenHolder.getCache().assertUpdates();
    }

    @Test
    public void testRefreshExpiredRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens(true, true).initCache();

        TokenResponse response = tokenHolder.createTokenResponse();
        Mockito.when(client.tokenRequest(null)).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).tokenRequest(null);
        Mockito.verifyNoMoreInteractions(client);
        Assertions.assertTrue(tokenHolder.getCache().isUpdated());
    }

    @Test
    public void testCached() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens().initCache();

        String token = tokenManager.getToken(TokenType.ACCESS, false, false);
        Assertions.assertEquals(tokenHolder.getAccessToken(), token);

        Mockito.verifyNoMoreInteractions(client);
        Assertions.assertFalse(tokenHolder.getCache().isUpdated());
    }

    @Test
    public void testCachedForceRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens().initCache().initTokens();

        String cachedRefreshToken = tokenHolder.getCache().getRefreshToken();
        String cachedAccessToken = tokenHolder.getCache().getAccessToken();
        TokenResponse response = tokenHolder.createTokenResponse();
        Mockito.when(client.refreshRequest(cachedRefreshToken, null)).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, true, false);

        Assertions.assertNotEquals(cachedAccessToken, token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(cachedRefreshToken, null);
        Mockito.verifyNoMoreInteractions(client);
        tokenHolder.getCache().assertUpdates();
    }

    @Test
    public void testCachedForceRefreshWithInvalidRefresh() throws OpenIDException, TokenManagerException, TokenCacheException {
        tokenManager = new TokenManager(context, tokenHolder.getCache(), client);
        tokenHolder.initTokens().initCache().initTokens();

        TokenResponse response = new TokenResponse();
        response.setError("invalid");

        String cachedRefreshToken = tokenHolder.getCache().getRefreshToken();
        String cachedAccessToken = tokenHolder.getCache().getAccessToken();

        Mockito.when(client.refreshRequest(cachedRefreshToken, null)).thenThrow(new OpenIDException("Invalid token"));

        response = tokenHolder.createTokenResponse();
        Mockito.when(client.tokenRequest(null)).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, true, false);

        Assertions.assertNotEquals(cachedAccessToken, token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(cachedRefreshToken, null);
        Mockito.verify(client, Mockito.times(1)).tokenRequest(null);
        Mockito.verifyNoMoreInteractions(client);
    }

}