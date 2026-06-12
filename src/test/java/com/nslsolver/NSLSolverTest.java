package com.nslsolver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nslsolver.exceptions.AuthenticationException;
import com.nslsolver.exceptions.SolveException;
import com.nslsolver.models.RecaptchaV3Params;
import com.nslsolver.models.RecaptchaV3Result;
import com.nslsolver.models.TurnstileParams;
import com.nslsolver.models.TurnstileResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests request-body construction and response parsing against a local HTTP server. */
class NSLSolverTest {

    private HttpServer server;
    private String baseUrl;
    private volatile JsonObject lastRequestBody;
    private volatile String lastPath;
    private String responseBody;
    private int responseStatus;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", new RecordingHandler());
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        responseStatus = 200;
        responseBody = "{}";
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    private NSLSolver client() {
        return NSLSolver.builder("nsl_test_key")
                .baseUrl(baseUrl)
                .maxRetries(0)
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    @Test
    void turnstileParsesSolveTimeMsAndOmitsExtraFields() throws Exception {
        responseBody = "{\"success\":true,\"token\":\"tok_abc\",\"solve_time_ms\":247}";

        TurnstileResult result = client().solveTurnstile(
                TurnstileParams.builder().siteKey("0x4AAA").url("https://example.com").build());

        // request body shape
        assertEquals("turnstile", lastRequestBody.get("type").getAsString());
        assertEquals("0x4AAA", lastRequestBody.get("site_key").getAsString());
        assertEquals("https://example.com", lastRequestBody.get("url").getAsString());
        assertFalse(lastRequestBody.has("proxy"), "proxy must be omitted when unset");

        // response parsing
        assertTrue(result.isSuccess());
        assertEquals("tok_abc", result.getToken());
        assertEquals(Long.valueOf(247L), result.getSolveTimeMs());
        assertEquals(0.0, result.getCost()); // cost absent -> 0.0
        assertNull(result.getType());        // type absent -> null
    }

    @Test
    void recaptchaV3SendsCorrectBodyAndParsesHyphenatedType() throws Exception {
        responseBody = "{\"success\":true,\"token\":\"03AGdBq2\",\"action\":\"login\",\"type\":\"recaptcha-v3\"}";

        RecaptchaV3Result result = client().solveRecaptchaV3(
                RecaptchaV3Params.builder()
                        .siteKey("6Lc_site")
                        .url("https://example.com")
                        .proxy("http://user:pass@host:8080")
                        .action("login")
                        .enterprise(true)
                        .userAgent("Mozilla/5.0")
                        .build());

        assertEquals("/solve", lastPath);
        assertEquals("recaptchav3", lastRequestBody.get("type").getAsString());
        assertEquals("6Lc_site", lastRequestBody.get("site_key").getAsString());
        assertEquals("https://example.com", lastRequestBody.get("url").getAsString());
        assertEquals("http://user:pass@host:8080", lastRequestBody.get("proxy").getAsString());
        assertEquals("login", lastRequestBody.get("action").getAsString());
        assertTrue(lastRequestBody.get("enterprise").getAsBoolean()); // real JSON boolean
        assertTrue(lastRequestBody.get("enterprise").getAsJsonPrimitive().isBoolean());
        assertEquals("Mozilla/5.0", lastRequestBody.get("user_agent").getAsString());

        assertTrue(result.isSuccess());
        assertEquals("03AGdBq2", result.getToken());
        assertEquals("login", result.getAction());
        assertEquals("recaptcha-v3", result.getType()); // hyphenated slug, not the discriminator
    }

    @Test
    void recaptchaV3OmitsActionAndEnterpriseWhenUnset() throws Exception {
        responseBody = "{\"success\":true,\"token\":\"03AGdBq2\",\"type\":\"recaptcha-v3\"}";

        client().solveRecaptchaV3(
                RecaptchaV3Params.builder()
                        .siteKey("6Lc_site")
                        .url("https://example.com")
                        .proxy("http://host:8080")
                        .build());

        assertFalse(lastRequestBody.has("action"), "action omitted when unset (server defaults to verify)");
        assertFalse(lastRequestBody.has("enterprise"), "enterprise omitted when false");
        assertFalse(lastRequestBody.has("user_agent"), "user_agent omitted when unset");
    }

    @Test
    void mapsErrorStatusToTypedException() {
        responseStatus = 401;
        responseBody = "{\"error\":\"invalid api key\"}";

        assertThrows(AuthenticationException.class, () -> client().solveTurnstile(
                TurnstileParams.builder().siteKey("x").url("https://example.com").build()));
    }

    @Test
    void nonJsonErrorBodyStillThrowsTypedException() {
        responseStatus = 400;
        responseBody = "<html>bad request</html>";

        assertThrows(SolveException.class, () -> client().solveTurnstile(
                TurnstileParams.builder().siteKey("x").url("https://example.com").build()));
    }

    private final class RecordingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            lastPath = exchange.getRequestURI().getPath();
            String reqBody = readBody(exchange.getRequestBody());
            lastRequestBody = reqBody.isEmpty()
                    ? new JsonObject()
                    : JsonParser.parseString(reqBody).getAsJsonObject();

            byte[] out = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus, out.length);
            exchange.getResponseBody().write(out);
            exchange.close();
        }

        private String readBody(InputStream in) throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] chunk = new byte[1024];
            int n;
            while ((n = in.read(chunk)) != -1) {
                buf.write(chunk, 0, n);
            }
            return new String(buf.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
