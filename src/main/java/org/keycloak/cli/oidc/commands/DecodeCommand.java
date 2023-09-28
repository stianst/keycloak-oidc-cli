package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.User;
import org.keycloak.cli.oidc.oidc.TokenParser;
import picocli.CommandLine;

@CommandLine.Command(name = "decode")
public class DecodeCommand implements Runnable {

    @CommandLine.Option(names = {"--jwt"}, description = "Token type to return", required = true)
    String jwt;

    @Override
    public void run() {
        String decoded = TokenParser.parse(jwt).getJwtDecoded();
        User.cli().print(decoded);
    }

}
