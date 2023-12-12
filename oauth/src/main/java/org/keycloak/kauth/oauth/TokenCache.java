package org.keycloak.kauth.oauth;

import org.keycloak.kauth.oauth.exceptions.TokenCacheException;

public interface TokenCache {

    String getIssuer();

    String getClientId();

    String getRefreshToken();

    String getRefreshScope();

    String getIdToken();

    String getAccessToken();

    String getTokenScope();

    void update(String issuer, String clientId, String refreshToken, String accessToken, String idToken, String refreshScope, String tokenScope) throws TokenCacheException;

}
