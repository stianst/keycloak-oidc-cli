package org.keycloak.oidc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.oidc.config.ConfigException;
import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.oidc.OpenIDClient;
import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import org.keycloak.cli.oidc.oidc.TokenManager;
import org.keycloak.cli.oidc.oidc.TokenType;
import org.keycloak.cli.oidc.oidc.exceptions.OpenIDException;
import org.keycloak.cli.oidc.oidc.exceptions.TokenManagerException;
import org.keycloak.cli.oidc.oidc.representations.TokenResponse;
import org.keycloak.cli.oidc.oidc.representations.jwt.JwtClaims;
import org.keycloak.oidc.mock.FakeJwt;
import org.keycloak.oidc.mock.OpenIDTestProviderExtension;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

@ExtendWith(OpenIDTestProviderExtension.class)
public class TokenManagerTest {

    private FakeJwt fakeJwt = new FakeJwt("https://myissuer", new ObjectMapper());
    private TokenManager tokenManager;
    private Context context;
    private OpenIDClient client;
    private ConfigHandler configHandler;

    @BeforeEach
    public void before() {
        context = OpenIDTestUtils.createContext("https://myissuer", OpenIDFlow.RESOURCE_OWNER);
        client = Mockito.mock(OpenIDClient.class);
        configHandler = Mockito.mock(ConfigHandler.class);

        tokenManager = new TokenManager(context, configHandler, client);
    }

    @Test
    public void testNoSavedOffline() {
        TokenManagerException exception = Assertions.assertThrows(TokenManagerException.class, () -> tokenManager.getToken(TokenType.ACCESS, true));
        Assertions.assertEquals("No cached token", exception.getMessage());

        Mockito.verifyNoInteractions(client, configHandler);
    }

    @Test
    public void testSavedOffline() throws OpenIDException, TokenManagerException, ConfigException {
        String refreshToken = createToken(TokenType.REFRESH, false);
        String accessToken = createToken(TokenType.ACCESS, false);
        String idToken = createToken(TokenType.ID, false);

        context.setAccessToken(accessToken);
        context.setRefreshToken(refreshToken);
        context.setIdToken(idToken);

        Assertions.assertEquals(refreshToken, tokenManager.getToken(TokenType.REFRESH, true));
        Assertions.assertEquals(accessToken, tokenManager.getToken(TokenType.ACCESS, true));
        Assertions.assertEquals(idToken, tokenManager.getToken(TokenType.ID, true));

        Mockito.verifyNoInteractions(client, configHandler);
    }

    @Test
    public void testTokenRequest() throws OpenIDException, TokenManagerException, ConfigException {
        TokenResponse tokenResponse = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false));
        Assertions.assertEquals(tokenResponse.getRefreshToken(), tokenManager.getToken(TokenType.REFRESH, false));
        Assertions.assertEquals(tokenResponse.getIdToken(), tokenManager.getToken(TokenType.ID, false));

        Mockito.verify(client, Mockito.times(3)).tokenRequest();
        Mockito.verify(configHandler, Mockito.never()).save();
        Mockito.verifyNoMoreInteractions(client, configHandler);
    }

    @Test
    public void testTokenRequestCacheTokens() throws OpenIDException, TokenManagerException, ConfigException {
        context.setStoreTokens(true);
        TokenResponse tokenResponse = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(tokenResponse);

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false));

        Assertions.assertEquals(tokenResponse.getRefreshToken(), context.getRefreshToken());
        Assertions.assertEquals(tokenResponse.getAccessToken(), context.getAccessToken());
        Assertions.assertEquals(tokenResponse.getIdToken(), context.getIdToken());

        Assertions.assertEquals(tokenResponse.getAccessToken(), tokenManager.getToken(TokenType.ACCESS, false));

        Mockito.verify(client, Mockito.times(1)).tokenRequest();
        Mockito.verify(configHandler, Mockito.times(1)).save();
        Mockito.verifyNoMoreInteractions(client, configHandler);
    }

    @Test
    public void testRefresh() throws OpenIDException, TokenManagerException, ConfigException {
        context.setRefreshToken(createToken(TokenType.REFRESH, false));
        context.setRefreshToken(createToken(TokenType.ACCESS, true));
        context.setRefreshToken(createToken(TokenType.ID, false));

        TokenResponse response = createTokenResponse();
        Mockito.when(client.refreshRequest(context.getRefreshToken())).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).refreshRequest(context.getRefreshToken());
        Mockito.verifyNoMoreInteractions(client, configHandler);
    }

    @Test
    public void testRefreshExpiredRefresh() throws OpenIDException, TokenManagerException, ConfigException {
        context.setRefreshToken(createToken(TokenType.REFRESH, true));
        context.setRefreshToken(createToken(TokenType.ACCESS, true));
        context.setRefreshToken(createToken(TokenType.ID, true));

        TokenResponse response = createTokenResponse();
        Mockito.when(client.tokenRequest()).thenReturn(response);

        String token = tokenManager.getToken(TokenType.ACCESS, false);
        Assertions.assertEquals(response.getAccessToken(), token);

        Mockito.verify(client, Mockito.times(1)).tokenRequest();
        Mockito.verifyNoMoreInteractions(client, configHandler);
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