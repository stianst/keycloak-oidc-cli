package org.keycloak.cli.oidc.commands;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@QuarkusMainTest
public class DecodeCommandTest {

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJscGZmYUtZbFpjcHZKM0ZSNGpPTS15cVBwS29oQ2Y0LTNrbFVCXzlmNzN3In0.eyJleHAiOjE2OTU4NzA3NjMsImlhdCI6MTY5NTg3MDcwMywianRpIjoiZWZjMGZmM2EtZmJkYy00NGNkLWJjOWEtOWViNzA0MWI5NGU4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9tYXN0ZXIiLCJzdWIiOiJhNTE0NjljYi04ZGZiLTQzYTktOGE0MC1iNmI4ZjAyMTk0ZjgiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1jbGkiLCJzZXNzaW9uX3N0YXRlIjoiNzliYjU5NDQtMmJlMC00ZmVhLWFmNWItMGNiODk1MzhmN2QwIiwiYWNyIjoiMSIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiI3OWJiNTk0NC0yYmUwLTRmZWEtYWY1Yi0wY2I4OTUzOGY3ZDAiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIn0.MXOX9ZtExn495rnpBwU1gTQtbir5NrGkmA3u4mSNvKkB5wb51A9u1qx6fVRJSSk9QhxiW_QrG5pQ4xZVP93zKxKVG8QUW3Kfh2_lEzc6TypszbZA43puOLbxc-MgJTONmWwiQ8ueLrGBIauwZQCtXOGFrgGpn3Bzo_QeITiDnGdCCwyVkLF4uGrTMv49WcTgaMxQfDPAwoFUuDezprCzrWdjO8upZQnT4-5Gq7Og910FKjGLSR-hDiq_TJXTv9BlWwevwsYBCfyVi7oY4u-x2ae6OpTR5pvakHRDjr9btFyFCIPOZr3u9qDLKdLz7XMgWHTx_rfjlWWxtPMSeyD_YQ";

    @Test
    public void testDecode(QuarkusMainLauncher launcher) throws IOException {
        LaunchResult result = launcher.launch("decode", "--jwt=" + TOKEN);

        String expectedOutput = new String(DecodeCommandTest.class.getResource("DecodeCommandTest-testDecode.txt").openStream().readAllBytes(), StandardCharsets.UTF_8);
        Assertions.assertEquals(expectedOutput, result.getOutput());
    }

}
