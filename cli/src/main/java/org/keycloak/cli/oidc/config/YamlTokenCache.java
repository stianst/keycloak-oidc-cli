package org.keycloak.cli.oidc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.LinkedList;
import java.util.List;

@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class YamlTokenCache {

    private List<YamlTokenCacheContext> contexts = new LinkedList<>();

    public List<YamlTokenCacheContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<YamlTokenCacheContext> contexts) {
        this.contexts = contexts;
    }

}
