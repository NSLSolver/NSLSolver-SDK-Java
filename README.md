# NSLSolver Java SDK

Java client for the [NSLSolver](https://nslsolver.com) captcha API. Supports Cloudflare Turnstile and Challenge pages.

Requires Java 11+.

## Installation

**Maven**

```xml
<dependency>
    <groupId>com.nslsolver</groupId>
    <artifactId>nslsolver-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle**

```groovy
implementation 'com.nslsolver:nslsolver-java:1.0.0'
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
System.out.println(result.getToken());

ChallengeResult challenge = solver.solveChallenge(
    ChallengeParams.builder()
        .url("https://example.com/protected")
        .proxy("http://user:pass@host:port")
        .build()
);
System.out.println(challenge.getCfClearance());

BalanceResult balance = solver.getBalance();
System.out.println(balance.getBalance());

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

## License

MIT
