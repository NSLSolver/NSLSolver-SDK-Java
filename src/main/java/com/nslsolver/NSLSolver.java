package com.nslsolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nslsolver.exceptions.*;
import com.nslsolver.models.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Client for the NSLSolver captcha solving API. Supports Turnstile and Challenge solving. */
public final class NSLSolver implements AutoCloseable {

    private static final String DEFAULT_BASE_URL = "https://api.nslsolver.com";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000;
    private static final double BACKOFF_MULTIPLIER = 2.0;
    private static final String SDK_VERSION = "1.0.0";
    private static final String USER_AGENT = "nslsolver-java/" + SDK_VERSION;

    private final String apiKey;
    private final String baseUrl;
    private final Duration timeout;
    private final int maxRetries;
    private final HttpClient httpClient;
    private final Gson gson;

    public NSLSolver(String apiKey) {
        this(new Builder(apiKey));
    }

    private NSLSolver(Builder builder) {
        this.apiKey = Objects.requireNonNull(builder.apiKey, "apiKey is required");
        this.baseUrl = builder.baseUrl != null ? builder.baseUrl : DEFAULT_BASE_URL;
        this.timeout = builder.timeout != null ? builder.timeout : DEFAULT_TIMEOUT;
        this.maxRetries = builder.maxRetries > 0 ? builder.maxRetries : DEFAULT_MAX_RETRIES;
        this.gson = new GsonBuilder().create();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    // --- Sync API ---

    /**
     * Solves a Cloudflare Turnstile captcha.
     * @throws AuthenticationException if the API key is invalid (401)
     * @throws InsufficientBalanceException if balance is too low (402)
     * @throws TypeNotAllowedException if Turnstile isn't enabled (403)
     * @throws RateLimitException if rate limited after retries (429)
     * @throws SolveException on bad request or backend failure (400/503)
     */
    public TurnstileResult solveTurnstile(TurnstileParams params) throws NSLSolverException {
        Objects.requireNonNull(params, "params must not be null");

        JsonObject body = new JsonObject();
        body.addProperty("type", "turnstile");
        body.addProperty("site_key", params.getSiteKey());
        body.addProperty("url", params.getUrl());

        if (params.getAction() != null) {
            body.addProperty("action", params.getAction());
        }
        if (params.getCdata() != null) {
            body.addProperty("cdata", params.getCdata());
        }
        if (params.getProxy() != null) {
            body.addProperty("proxy", params.getProxy());
        }
        if (params.getUserAgent() != null) {
            body.addProperty("user_agent", params.getUserAgent());
        }

        String responseBody = executeWithRetry("POST", "/solve", body.toString());

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        return new TurnstileResult(
                getStringOrNull(json, "token"),
                getStringOrNull(json, "type"),
                json.has("success") && json.get("success").getAsBoolean()
        );
    }

    /**
     * Solves a Cloudflare Challenge page. Proxy is required.
     * @throws AuthenticationException if the API key is invalid (401)
     * @throws InsufficientBalanceException if balance is too low (402)
     * @throws TypeNotAllowedException if Challenge isn't enabled (403)
     * @throws RateLimitException if rate limited after retries (429)
     * @throws SolveException on bad request or backend failure (400/503)
     */
    public ChallengeResult solveChallenge(ChallengeParams params) throws NSLSolverException {
        Objects.requireNonNull(params, "params must not be null");

        JsonObject body = new JsonObject();
        body.addProperty("type", "challenge");
        body.addProperty("url", params.getUrl());
        body.addProperty("proxy", params.getProxy());

        if (params.getUserAgent() != null) {
            body.addProperty("user_agent", params.getUserAgent());
        }

        String responseBody = executeWithRetry("POST", "/solve", body.toString());

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

        Map<String, String> cookies = new HashMap<>();
        if (json.has("cookies") && json.get("cookies").isJsonObject()) {
            JsonObject cookiesJson = json.getAsJsonObject("cookies");
            for (Map.Entry<String, JsonElement> entry : cookiesJson.entrySet()) {
                cookies.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        return new ChallengeResult(
                cookies,
                getStringOrNull(json, "user_agent"),
                getStringOrNull(json, "type"),
                json.has("success") && json.get("success").getAsBoolean()
        );
    }

    /**
     * Returns the current account balance and limits.
     * @throws AuthenticationException if the API key is invalid (401)
     */
    public BalanceResult getBalance() throws NSLSolverException {
        String responseBody = executeWithRetry("GET", "/balance", null);

        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

        double balance = json.has("balance") ? json.get("balance").getAsDouble() : 0.0;
        int maxThreads = json.has("max_threads") ? json.get("max_threads").getAsInt() : 0;

        List<String> allowedTypes = new ArrayList<>();
        if (json.has("allowed_types") && json.get("allowed_types").isJsonArray()) {
            JsonArray arr = json.getAsJsonArray("allowed_types");
            for (JsonElement el : arr) {
                allowedTypes.add(el.getAsString());
            }
        }

        return new BalanceResult(balance, maxThreads, allowedTypes);
    }

    // --- Async API ---

    public CompletableFuture<TurnstileResult> solveTurnstileAsync(TurnstileParams params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return solveTurnstile(params);
            } catch (NSLSolverException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<ChallengeResult> solveChallengeAsync(ChallengeParams params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return solveChallenge(params);
            } catch (NSLSolverException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<BalanceResult> getBalanceAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalance();
            } catch (NSLSolverException e) {
                throw new CompletionException(e);
            }
        });
    }

    // --- HTTP internals ---

    /** Retries on 429/503 with exponential backoff. */
    private String executeWithRetry(String method, String path, String body) throws NSLSolverException {
        NSLSolverException lastException = null;
        long backoffMs = INITIAL_BACKOFF_MS;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return execute(method, path, body);
            } catch (NSLSolverException e) {
                lastException = e;

                if (!e.isRetryable() || attempt == maxRetries) {
                    throw e;
                }

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new NSLSolverException("Request interrupted during retry backoff", ie);
                }

                backoffMs = (long) (backoffMs * BACKOFF_MULTIPLIER);
            }
        }

        throw lastException != null ? lastException : new NSLSolverException(0, "Unknown error");
    }

    private String execute(String method, String path, String body) throws NSLSolverException {
        try {
            URI uri = URI.create(baseUrl + path);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(timeout)
                    .header("X-API-Key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", USER_AGENT);

            if ("POST".equals(method) && body != null) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
            } else {
                requestBuilder.GET();
            }

            HttpResponse<String> response = httpClient.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            }

            String errorMessage = parseErrorMessage(responseBody, statusCode);
            throw createException(statusCode, errorMessage);

        } catch (NSLSolverException e) {
            throw e;
        } catch (IOException e) {
            throw new NSLSolverException("Network error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NSLSolverException("Request interrupted", e);
        } catch (Exception e) {
            throw new NSLSolverException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private String parseErrorMessage(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "HTTP " + statusCode;
        }

        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if (json.has("error")) {
                return json.get("error").getAsString();
            }
            if (json.has("message")) {
                return json.get("message").getAsString();
            }
        } catch (Exception ignored) {
        }

        return responseBody.length() > 200 ? responseBody.substring(0, 200) : responseBody;
    }

    private NSLSolverException createException(int statusCode, String message) {
        switch (statusCode) {
            case 400:
                return new SolveException(400, message);
            case 401:
                return new AuthenticationException(message);
            case 402:
                return new InsufficientBalanceException(message);
            case 403:
                return new TypeNotAllowedException(message);
            case 429:
                return new RateLimitException(message);
            case 503:
                return new SolveException(503, message);
            default:
                return new NSLSolverException(statusCode, message);
        }
    }

    private static String getStringOrNull(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    @Override
    public void close() {
        // HttpClient doesn't need explicit cleanup in Java 11+
    }

    // --- Builder ---

    public static final class Builder {

        private final String apiKey;
        private String baseUrl;
        private Duration timeout;
        private int maxRetries;

        private Builder(String apiKey) {
            this.apiKey = Objects.requireNonNull(apiKey, "apiKey is required");
        }

        /** Override the API base URL. Defaults to https://api.nslsolver.com */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /** Request timeout. Defaults to 120s. */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /** Max retries on 429/503. Defaults to 3. */
        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries must be >= 0");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        public NSLSolver build() {
            return new NSLSolver(this);
        }
    }
}
