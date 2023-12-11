package org.keycloak.cli.oidc.commands;

import org.keycloak.client.oauth.OpenIDFlow;
import org.keycloak.client.oauth.TokenType;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface EnumValues {

    String TOKEN_TYPE = Arrays.stream(TokenType.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", "));
    String OPENID_FLOW = Arrays.stream(OpenIDFlow.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", "));

}
