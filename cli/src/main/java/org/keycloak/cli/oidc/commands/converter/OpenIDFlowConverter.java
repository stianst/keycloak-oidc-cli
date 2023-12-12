package org.keycloak.cli.oidc.commands.converter;

import org.keycloak.kauth.oauth.OAuthFlow;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OpenIDFlowConverter implements CommandLine.ITypeConverter<OAuthFlow> {
    @Override
    public OAuthFlow convert(String s) {
        try {
            if (s.equals("null")) {
                return null;
            }
            return OAuthFlow.valueOf(s.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("valid values: " + Arrays.stream(OAuthFlow.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", ")));
        }
    }
}
