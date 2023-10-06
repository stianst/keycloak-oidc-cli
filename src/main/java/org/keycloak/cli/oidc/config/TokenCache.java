package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenCache {

    private List<TokenCacheContext> contexts = new LinkedList<>();

    public List<TokenCacheContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<TokenCacheContext> contexts) {
        this.contexts = contexts;
    }

}
