package org.keycloak.cli.oidc.oidc;

public interface OpenIDParams {

    String GRANT_TYPE = "grant_type";

    String SCOPE = "scope";

    String USERNAME = "username";

    String PASSWORD = "password";

    String REFRESH_TOKEN = "refresh_token";

    String CODE = "code";

    String REDIRECT_URI = "redirect_uri";

    String CODE_VERIFIER = "code_verifier";

    String CODE_CHALLENGE = "code_challenge";

    String CODE_CHALLENGE_METHOD = "code_challenge_method";

    String RESPONSE_TYPE = "response_type";

    String CLIENT_ID = "client_id";

    String STATE = "state";

    String NONCE = "nonce";

    String ERROR = "error";

    String DEVICE_CODE = "device_code";

    String TOKEN = "token";

}
