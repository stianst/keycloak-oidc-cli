package org.keycloak.cli.oidc.commands.config;

import picocli.CommandLine;

@CommandLine.Command(name = "config", subcommands = {
        ConfigSetCommand.class,
        ConfigUpdateCommand.class,
        ConfigUseCommand.class,
        ConfigCurrentCommand.class,
        ConfigViewCommand.class,
        ConfigDeleteCommand.class,
})
public class ConfigCommand {

}
