package org.keycloak.cli.oidc.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import org.keycloak.cli.oidc.commands.config.ConfigCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(name = "kc-oidc", mixinStandardHelpOptions = true, versionProvider = VersionProvider.class, subcommands = {
        TokenCommand.class,
        ConfigCommand.class,
        DecodeCommand.class,
        IntrospectCommand.class,
        DeleteTokensCommand.class
})
public class EntryCommand {

}
