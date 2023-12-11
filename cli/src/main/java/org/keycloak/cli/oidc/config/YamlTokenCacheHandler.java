package org.keycloak.cli.oidc.config;

import org.keycloak.client.oauth.exceptions.TokenCacheException;

import java.io.IOException;
import java.util.Iterator;

public class YamlTokenCacheHandler {

    private static YamlTokenCacheHandler yamlTokenCacheHandler;

    private YamlFileStore<YamlTokenCache> tokenStore;

    private YamlTokenCache yamlTokenCache;

    public YamlTokenCacheHandler() throws TokenCacheException {
        tokenStore = new YamlFileStore<>(Environment.getTokenCacheFile(), YamlTokenCache.class);
        reload();
    }

    public static YamlTokenCacheHandler get() throws TokenCacheException {
        if (yamlTokenCacheHandler == null) {
            yamlTokenCacheHandler = new YamlTokenCacheHandler();
        }
        return yamlTokenCacheHandler;
    }

    public static void clearInstance() {
        yamlTokenCacheHandler = null;
    }

    public YamlTokenCache getTokenCache() {
        return yamlTokenCache;
    }

    public YamlTokenCacheContext getTokenCacheContext(Context context) {
        for (YamlTokenCacheContext c : yamlTokenCache.getContexts()) {
            if (c.getContext().equals(context.getName())) {
                return c;
            }
        }

        YamlTokenCacheContext yamlTokenCacheContext = new YamlTokenCacheContext();
        yamlTokenCacheContext.setContext(context.getName());
        yamlTokenCache.getContexts().add(yamlTokenCacheContext);
        return yamlTokenCacheContext;
    }

    public void reload() throws TokenCacheException {
        try {
            yamlTokenCache = tokenStore.reload();
            if (yamlTokenCache == null) {
                yamlTokenCache = new YamlTokenCache();
            }
        } catch (IOException e) {
            throw new TokenCacheException("Failed to load token cache", e);
        }
    }

    public YamlTokenCacheHandler save() throws TokenCacheException {
        try {
            Iterator<YamlTokenCacheContext> itr = yamlTokenCache.getContexts().iterator();
            while (itr.hasNext()) {
                YamlTokenCacheContext yamlTokenCacheContext = itr.next();
                if (yamlTokenCacheContext.getIdToken() == null && yamlTokenCacheContext.getAccessToken() == null && yamlTokenCacheContext.getRefreshToken() == null) {
                    itr.remove();
                }
            }

            if (yamlTokenCache.getContexts().isEmpty()) {
                tokenStore.delete();
            } else {
                tokenStore.save(yamlTokenCache);
            }
            return this;
        } catch (IOException e) {
            throw new TokenCacheException("Failed to token cache", e);
        }
    }

    public void deleteTokens(Context context) throws TokenCacheException {
        Iterator<YamlTokenCacheContext> itr = yamlTokenCache.getContexts().iterator();
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
