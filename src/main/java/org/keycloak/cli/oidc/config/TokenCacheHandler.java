package org.keycloak.cli.oidc.config;

import java.io.IOException;
import java.util.Iterator;

public class TokenCacheHandler {

    private static TokenCacheHandler tokenCacheHandler;

    private YamlFileStore<TokenCache> tokenStore;

    private TokenCache tokenCache;

    public TokenCacheHandler() throws TokenCacheException {
        tokenStore = new YamlFileStore<>(Environment.getTokenCacheFile(), TokenCache.class);
        reload();
    }

    public static TokenCacheHandler get() throws TokenCacheException {
        if (tokenCacheHandler == null) {
            tokenCacheHandler = new TokenCacheHandler();
        }
        return tokenCacheHandler;
    }

    public static void clearInstance() {
        tokenCacheHandler = null;
    }

    public TokenCache getTokenCache() {
        return tokenCache;
    }

    public TokenCacheContext getTokenCacheContext(Context context) {
        for (TokenCacheContext c : tokenCache.getContexts()) {
            if (c.getContext().equals(context.getName())) {
                return c;
            }
        }

        TokenCacheContext tokenCacheContext = new TokenCacheContext();
        tokenCacheContext.setContext(context.getName());
        tokenCache.getContexts().add(tokenCacheContext);
        return tokenCacheContext;
    }

    public void reload() throws TokenCacheException {
        try {
            tokenCache = tokenStore.reload();
            if (tokenCache == null) {
                tokenCache = new TokenCache();
            }
        } catch (IOException e) {
            throw new TokenCacheException("Failed to load token cache", e);
        }
    }

    public TokenCacheHandler save() throws TokenCacheException {
        try {
            Iterator<TokenCacheContext> itr = tokenCache.getContexts().iterator();
            while (itr.hasNext()) {
                TokenCacheContext tokenCacheContext = itr.next();
                if (tokenCacheContext.getIdToken() == null && tokenCacheContext.getAccessToken() == null && tokenCacheContext.getRefreshToken() == null) {
                    itr.remove();
                }
            }

            if (tokenCache.getContexts().isEmpty()) {
                tokenStore.delete();
            } else {
                tokenStore.save(tokenCache);
            }
            return this;
        } catch (IOException e) {
            throw new TokenCacheException("Failed to token cache", e);
        }
    }

    public void deleteTokens(Context context) throws TokenCacheException {
        Iterator<TokenCacheContext> itr = tokenCache.getContexts().iterator();
        while (itr.hasNext()) {
            if (itr.next().getContext().equals(context.getName())) {
                itr.remove();
            }
        }
        save();
    }

    public void deleteTokens() {
        tokenStore.delete();
    }

}
