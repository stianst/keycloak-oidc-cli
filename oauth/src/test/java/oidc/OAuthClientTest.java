package oidc;

import com.fasterxml.jackson.databind.ObjectMapper;
import oidc.mock.FakeJwt;
import oidc.mock.MockWeb;
import oidc.mock.OpenIDTestProviderExtension;
import oidc.mock.RequestHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.kauth.http.HttpHeaders;
import org.keycloak.kauth.http.server.HttpRequest;
import org.keycloak.kauth.oauth.OAuthClient;
import org.keycloak.kauth.oauth.OAuthFlow;
import org.keycloak.kauth.oauth.OAuthRequest;
import org.keycloak.kauth.oauth.TokenParser;
import org.keycloak.kauth.oauth.TokenType;
import org.keycloak.kauth.oauth.User;
import org.keycloak.kauth.oauth.exceptions.OpenIDException;
import org.keycloak.kauth.oauth.representations.TokenResponse;
import org.keycloak.kauth.oauth.representations.UserInfoResponse;
import org.keycloak.kauth.oauth.representations.jwt.Jwt;
import org.keycloak.kauth.oauth.representations.jwt.JwtClaims;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ExtendWith(OpenIDTestProviderExtension.class)
public class OAuthClientTest {

    @Test
    public void testAuthZ(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        MockWeb web = new MockWeb();
        User.setWeb(web);

        OAuthRequest context = OpenIDTestUtils.createContext(issuerUrl, OAuthFlow.AUTHORIZATION_CODE);
        OAuthClient client = new OAuthClient(context);

        requestHandler.expectWellKnown();
        requestHandler.expectAuthzRequest();
        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();

        Assertions.assertNotNull(requestHandler.pollRequest());

        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest authzRequest = requestHandler.pollRequest();
        String redirectUri = authzRequest.getQueryParams().get("redirect_uri");
        Assertions.assertEquals("theclient", authzRequest.getQueryParams().get("client_id"));
        Assertions.assertEquals("openid", authzRequest.getQueryParams().get("scope"));
        Assertions.assertNotNull(authzRequest.getQueryParams().get("state"));
        Assertions.assertNotNull(redirectUri);

        HttpRequest tokenRequest = requestHandler.pollRequest();
        Assertions.assertEquals("authorization_code", tokenRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals(redirectUri, tokenRequest.getBodyParams().get("redirect_uri"));
        assertBasicAuthorization(tokenRequest.getHeaderParams().get("Authorization"), "theclient", "thesecret");

        User.setWeb((User.Web) null);
    }

    @Test
    public void testResourceOwner(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        OAuthRequest context = OpenIDTestUtils.createContext(issuerUrl, OAuthFlow.RESOURCE_OWNER);
        OAuthClient client = new OAuthClient(context);

        requestHandler.expectWellKnown();
        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();

        Assertions.assertNotNull(requestHandler.pollRequest());

        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest httpRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(httpRequest);
        Assertions.assertEquals("password", httpRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("theuser", httpRequest.getBodyParams().get("username"));
        Assertions.assertEquals("thepassword", httpRequest.getBodyParams().get("password"));
        Assertions.assertEquals("openid", httpRequest.getBodyParams().get("scope"));
    }

    @Test
    public void testClientCredential(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        OAuthRequest context = OpenIDTestUtils.createContext(issuerUrl, OAuthFlow.CLIENT_CREDENTIAL);
        OAuthClient client = new OAuthClient(context);

        requestHandler.expectWellKnown();
        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();

        Assertions.assertNotNull(requestHandler.pollRequest());

        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest httpRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(httpRequest);
        Assertions.assertEquals("client_credentials", httpRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("openid", httpRequest.getBodyParams().get("scope"));
        assertBasicAuthorization(httpRequest.getHeaderParams().get(HttpHeaders.AUTHORIZATION), "theclient", "thesecret");
    }

    @Test
    public void testDevice(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        OAuthRequest context = OpenIDTestUtils.createContext(issuerUrl, OAuthFlow.DEVICE);
        OAuthClient client = new OAuthClient(context);

        requestHandler.expectWellKnown();
        requestHandler.expectDeviceAuthz();
        requestHandler.expectTokenRequestFailure("authorization_pending");
        requestHandler.expectTokenRequest();

        TokenResponse tokenResponse = client.tokenRequest();

        Assertions.assertNotNull(requestHandler.pollRequest());

        Assertions.assertEquals(TokenType.ACCESS.toString(), parse(tokenResponse.getAccessToken()).getClaims().getClaims().get("typ"));

        HttpRequest deviceRequest = requestHandler.pollRequest();
        assertBasicAuthorization(deviceRequest.getHeaderParams().get(HttpHeaders.AUTHORIZATION), "theclient", "thesecret");
        Assertions.assertEquals("openid", deviceRequest.getBodyParams().get("scope"));

        HttpRequest failedTokenRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(failedTokenRequest);

        HttpRequest tokenRequest = requestHandler.pollRequest();
        Assertions.assertNotNull(tokenRequest);
        Assertions.assertEquals("urn:ietf:params:oauth:grant-type:device_code", tokenRequest.getBodyParams().get("grant_type"));
        Assertions.assertEquals("openid", tokenRequest.getBodyParams().get("scope"));
        assertBasicAuthorization(tokenRequest.getHeaderParams().get(HttpHeaders.AUTHORIZATION), "theclient", "thesecret");
    }

    @Test
    public void testUserInfo(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws OpenIDException {
        OAuthRequest context = OpenIDTestUtils.createContext(issuerUrl, OAuthFlow.DEVICE);
        OAuthClient client = new OAuthClient(context);

        requestHandler.expectWellKnown();
        requestHandler.expectUserInfoRequest();

        FakeJwt fakeJwt = new FakeJwt(issuerUrl, new ObjectMapper());

        JwtClaims claims = new JwtClaims();
        claims.setSub("thesub");
        claims.getClaims().put("name", "thename");
        claims.getClaims().put("preferred_username", "thepreferredusername");
        claims.getClaims().put("random", "therandom");
        String token = fakeJwt.create(TokenType.ACCESS, claims);

        UserInfoResponse userInfoResponse = client.userInfoRequest(token);
        Assertions.assertEquals("thesub", userInfoResponse.getSub());
        Assertions.assertEquals("thename", userInfoResponse.getName());
        Assertions.assertEquals("thepreferredusername", userInfoResponse.getPreferredUsername());
        Assertions.assertEquals("therandom", userInfoResponse.getClaims().get("random"));
    }

    private Jwt parse(String token) {
        return TokenParser.parse(token).getJwt();
    }

    private void assertBasicAuthorization(String header, String username, String password) {
        Assertions.assertNotNull(header);
        String[] s = header.split(" ");
        Assertions.assertEquals("Basic", s[0]);
        String value = new String(Base64.getDecoder().decode(s[1]), StandardCharsets.UTF_8);
        Assertions.assertEquals(username + ":" + password, value);
    }

}
