package org.keycloak.cli.oidc.commands.converter;

import org.keycloak.client.oauth.OpenIDFlow;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.stream.Collectors;

public class OpenIDFlowConverter implements CommandLine.ITypeConverter<OpenIDFlow> {
    @Override
    public OpenIDFlow convert(String s) {
        try {
            if (s.equals("null")) {
                return null;
            }
            return OpenIDFlow.valueOf(s.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException("valid values: " + Arrays.stream(OpenIDFlow.values()).map(t -> t.toString().toLowerCase().replace('_', '-')).collect(Collectors.joining(", ")));
        }
    }
}
