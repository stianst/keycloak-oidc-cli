package org.keycloak.cli.oidc.commands;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import org.keycloak.cli.oidc.QuarkusVersionProvider;
import org.keycloak.cli.oidc.commands.config.ConfigCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, versionProvider = QuarkusVersionProvider.class, subcommands = {
        TokenCommand.class,
        ConfigCommand.class,
        DecodeCommand.class,
        IntrospectCommand.class
})
public class EntryCommand {

}
