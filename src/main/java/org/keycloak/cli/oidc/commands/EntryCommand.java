package org.keycloak.cli.oidc.commands;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.keycloak.cli.oidc.commands.config.ConfigCommand;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command(name = "kc-oidc", mixinStandardHelpOptions = true, versionProvider = VersionProvider.class, subcommands = {
        TokenCommand.class,
        ConfigCommand.class,
        DecodeCommand.class,
        IntrospectCommand.class,
        UserInfoCommand.class,
        DeleteTokensCommand.class
})
public class EntryCommand implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        CommandLine commandLine = new CommandLine(this, factory);
        commandLine.setExecutionExceptionHandler(new CommandExceptionHandler());
        return commandLine.execute(args);
    }
}
