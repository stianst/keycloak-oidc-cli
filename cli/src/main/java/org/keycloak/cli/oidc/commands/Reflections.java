package org.keycloak.cli.oidc.commands;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.keycloak.kauth.oauth.OAuthFlow;

@RegisterForReflection(targets = {
    org.keycloak.kauth.oauth.representations.jwt.JwtClaims.class,
    org.keycloak.kauth.oauth.representations.jwt.JwtHeader.class,
    org.keycloak.kauth.oauth.representations.jwt.Jwt.class,
    org.keycloak.kauth.oauth.representations.UserInfoResponse.class,
    org.keycloak.kauth.oauth.representations.TokenResponse.class,
    org.keycloak.kauth.oauth.representations.StringOrArrayDeserializer.class,
    org.keycloak.kauth.oauth.representations.StringOrArraySerializer.class,
    org.keycloak.kauth.oauth.representations.DeviceAuthorizationResponse.class,
    org.keycloak.kauth.oauth.representations.WellKnown.class,
    org.keycloak.kauth.oauth.representations.TokenIntrospectionResponse.class,
    OAuthFlow.class,
})
public class Reflections {
}
