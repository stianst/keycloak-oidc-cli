package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.oidc.TokenParser;
import picocli.CommandLine;

@CommandLine.Command(name = "decode", description = "Converts the base64 encoded JWT into a JSON document")
public class DecodeCommand implements Runnable {

    @CommandLine.Option(names = {"--token"}, description = "Token to decode", required = true)
    String token;

    @Override
    public void run() {
        String decoded = TokenParser.parse(token).getJwtDecoded();
        User.cli().print(decoded);
    }

}
