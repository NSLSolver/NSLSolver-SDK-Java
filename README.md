# NSLSolver Java SDK

Java client for the [NSLSolver](https://nslsolver.com) captcha API. Supports Cloudflare Turnstile, Cloudflare Challenge pages, Kasada, Akamai Bot Manager, and reCAPTCHA v3 (incl. Enterprise).

Requires Java 11+.

## Installation

**Maven**

```xml
<dependency>
    <groupId>com.nslsolver</groupId>
    <artifactId>nslsolver-java</artifactId>
    <version>1.1.0</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.nslsolver:nslsolver-java:1.1.0'
```

## Usage

```java
NSLSolver solver = new NSLSolver("your-api-key");

TurnstileResult result = solver.solveTurnstile(
    TurnstileParams.builder()
        .siteKey("0x4AAAAAAAB...")
        .url("https://example.com")
        .build()
);
System.out.println(result.getToken() + " (cost: $" + result.getCost() + ")");

ChallengeResult challenge = solver.solveChallenge(
    ChallengeParams.builder()
        .url("https://example.com/protected")
        .proxy("http://user:pass@host:port")
        .build()
);
System.out.println(challenge.getCfClearance());

KasadaResult kasada = solver.solveKasada(
    KasadaParams.builder()
        .url("https://example.com/api")
        .userAgent("Mozilla/5.0 ... Chrome/130.0.0.0 ...")
        .uaVersion(130)
        .kasadaConfig(KasadaConfig.builder()
            .pJsPath("/149e9513-01fa-4fb0-aad4-566afd725d1b/2d206a39-8ed7-437e-a3be-862e0f06eea3/p.js")
            .fpHost("passport.twitch.tv")   // bare hostname, no scheme
            .tlHost("gql.twitch.tv")        // bare hostname, no scheme
            .build())
        .build()
);
System.out.println(kasada.getHeaders());

AkamaiResult akamai = solver.solveAkamai(
    AkamaiParams.builder()
        .url("https://example.com")
        .userAgent("Mozilla/5.0 ... Chrome/130.0.0.0 ...")
        .proxy("http://user:pass@host:port")
        .build()
);
System.out.println(akamai.getAbck()); // the _abck cookie, bound to the proxy + UA

RecaptchaV3Result recaptcha = solver.solveRecaptchaV3(
    RecaptchaV3Params.builder()
        .siteKey("6Lc...")
        .url("https://example.com")
        .proxy("http://user:pass@host:port")
        .action("login")      // optional; server defaults to "verify"
        .enterprise(false)     // set true for reCAPTCHA Enterprise
        .build()
);
System.out.println(recaptcha.getToken());

BalanceResult balance = solver.getBalance();
System.out.printf("$%.4f  CPM: %d/%d  unlimited=%s%n",
    balance.getBalance(),
    balance.getCurrentCpm(),
    balance.getCpmLimit(),
    balance.isUnlimited());

solver.close();
```

Implements `AutoCloseable` so try-with-resources works.

## Configuration

```java
NSLSolver solver = NSLSolver.builder("your-api-key")
    .timeout(Duration.ofSeconds(180))
    .maxRetries(5)
    .build();
```

Defaults: 120s timeout, 3 retries.

## Async

Every method has an async variant returning `CompletableFuture`:

```java
solver.solveTurnstileAsync(params)
    .thenAccept(r -> System.out.println(r.getToken()))
    .exceptionally(t -> { System.err.println(t.getCause()); return null; });
```

## Errors

All exceptions extend `NSLSolverException`. 429 and 503 are retried automatically with exponential backoff.

```java
try {
    TurnstileResult result = solver.solveTurnstile(params);
} catch (AuthenticationException e) {
    // bad api key (401)
} catch (InsufficientBalanceException e) {
    // add funds (402)
} catch (RateLimitException e) {
    // 429, all retries exhausted
} catch (SolveException e) {
    // 400 bad request or 503 backend error
} catch (NSLSolverException e) {
    // catch-all
}
```

## Documentation

For more information, check out the full documentation at https://docs.nslsolver.com

## License

MIT
