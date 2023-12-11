package cli.oidc.commands;

import io.quarkus.test.junit.main.QuarkusMainTest;

@QuarkusMainTest
//@ExtendWith({ OpenIDTestProviderExtension.class, ConfigHandlerExtension.class })
public class UserInfoCommandTest {
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//    private String token;
//    private FakeJwt fakeJwt;
//
//    @BeforeEach
//    public void before(@OpenIDTestProviderExtension.IssuerUrl String issuerUrl, @OpenIDTestProviderExtension.Requests RequestHandler requestHandler) throws ConfigException, TokenCacheException {
//        fakeJwt = new FakeJwt(issuerUrl, new ObjectMapper());
//
//        JwtClaims claims = new JwtClaims();
//        claims.setSub("thesub");
//        claims.getClaims().put("name", "thename");
//        claims.getClaims().put("preferred_username", "thepreferredusername");
//        claims.getClaims().put("random", "therandom");
//
//        token = fakeJwt.create(TokenType.ACCESS, claims);
//
//        ConfigHandler configHandler = ConfigHandler.get();
//        Context context = configHandler.getCurrentContext();
//        context.setIssuer(issuerUrl);
//        configHandler.save();
//
//        TokenCacheHandler tokenCacheHandler = TokenCacheHandler.get();
//        tokenCacheHandler.getTokenCacheContext(context).setAccessToken(token);
//        tokenCacheHandler.save();
//
//        requestHandler.expectWellKnown();
//        requestHandler.expectUserInfoRequest();
//    }
//
//    @Test
//    public void testUserInfoCachedToken(QuarkusMainLauncher launcher) throws JsonProcessingException {
//        LaunchResult result = launcher.launch("userinfo");
//
//        Assertions.assertEquals(0, result.exitCode());
//
//        UserInfoResponse userInfoResponse = objectMapper.readValue(result.getOutput(), UserInfoResponse.class);
//        Assertions.assertEquals("thesub", userInfoResponse.getSub());
//        Assertions.assertEquals("thename", userInfoResponse.getName());
//        Assertions.assertEquals("thepreferredusername", userInfoResponse.getPreferredUsername());
//        Assertions.assertEquals("therandom", userInfoResponse.getClaims().get("random"));
//    }
//
//    @Test
//    public void testUserInfoToken(QuarkusMainLauncher launcher) throws JsonProcessingException {
//        JwtClaims claims = new JwtClaims();
//        claims.setSub("thesub2");
//        claims.getClaims().put("name", "thename2");
//        claims.getClaims().put("preferred_username", "thepreferredusername2");
//        claims.getClaims().put("random", "therandom2");
//
//        String token = fakeJwt.create(TokenType.ACCESS, claims);
//
//        LaunchResult result = launcher.launch("userinfo", "--token=" + token);
//        Assertions.assertEquals(0, result.exitCode());
//
//        UserInfoResponse userInfoResponse = objectMapper.readValue(result.getOutput(), UserInfoResponse.class);
//        Assertions.assertEquals("thesub2", userInfoResponse.getSub());
//        Assertions.assertEquals("thename2", userInfoResponse.getName());
//        Assertions.assertEquals("thepreferredusername2", userInfoResponse.getPreferredUsername());
//        Assertions.assertEquals("therandom2", userInfoResponse.getClaims().get("random"));
//    }

}
