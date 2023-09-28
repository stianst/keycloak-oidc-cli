package org.keycloak.cli.oidc.commands;

import org.keycloak.cli.oidc.oidc.OpenIDFlow;
import org.keycloak.cli.oidc.oidc.TokenType;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface EnumValues {

    String TOKEN_TYPE = Arrays.stream(TokenType.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", "));
    String OPENID_FLOW = Arrays.stream(OpenIDFlow.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", "));

}
