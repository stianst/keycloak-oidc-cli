package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.config.ConfigHandler;
import org.keycloak.cli.oidc.config.Context;
import org.keycloak.cli.oidc.config.YamlTokenCacheHandler;
import picocli.CommandLine;

@CommandLine.Command(name = "delete-tokens", description = "Deletes cached tokens")
public class DeleteTokensCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Delete only for a given configuration context")
    String contextName;

    @Override
    public void run() {
        try {
            ConfigHandler configHandler = ConfigHandler.get();
            YamlTokenCacheHandler yamlTokenCacheHandler = YamlTokenCacheHandler.get();
            if (contextName != null) {
                Context context = configHandler.getContext(contextName);
                yamlTokenCacheHandler.deleteTokens(context);
            } else {
                yamlTokenCacheHandler.deleteTokens();
            }
        } catch (Exception e) {
            throw new CommandFailedException(e);
        }
    }

}
