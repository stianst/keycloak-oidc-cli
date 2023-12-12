package org.keycloak.kauth.oauth.representations.jwt;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JwtHeader {

    private String alg;

    private String kid;

    private String typ;

    protected Map<String, Object> claims = new TreeMap<>();

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    @JsonAnyGetter
    public Map<String, Object> getClaims() {
        return claims;
    }

    @JsonAnySetter
    public void setClaims(String name, Object value) {
        claims.put(name, value);
    }

}
