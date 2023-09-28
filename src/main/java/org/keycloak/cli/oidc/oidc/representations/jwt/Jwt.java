package org.keycloak.cli.oidc.oidc.representations.jwt;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Jwt {

    private JwtHeader header;
    private JwtClaims claims;
    private String signature;

    public JwtHeader getHeader() {
        return header;
    }

    public void setHeader(JwtHeader header) {
        this.header = header;
    }

    public JwtClaims getClaims() {
        return claims;
    }

    public void setClaims(JwtClaims claims) {
        this.claims = claims;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
