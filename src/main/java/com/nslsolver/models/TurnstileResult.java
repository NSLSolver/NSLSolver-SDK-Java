package com.nslsolver.models;

/** Result of a Turnstile solve. The token goes in the {@code cf-turnstile-response} form field. */
public final class TurnstileResult {

    private final String token;
    private final String type;
    private final boolean success;
    private final double cost;
    private final Long solveTimeMs;

    /** Backward-compatible constructor; leaves {@code solveTimeMs} unset. */
    public TurnstileResult(String token, String type, boolean success, double cost) {
        this(token, type, success, cost, null);
    }

    public TurnstileResult(String token, String type, boolean success, double cost, Long solveTimeMs) {
        this.token = token;
        this.type = type;
        this.success = success;
        this.cost = cost;
        this.solveTimeMs = solveTimeMs;
    }

    public String getToken() { return token; }

    /**
     * The response type slug, if the API returned one. The Turnstile success
     * response typically omits {@code type}, so this is usually {@code null}.
     */
    public String getType() { return type; }
    public boolean isSuccess() { return success; }

    /**
     * USD deducted from the account balance for this solve, or {@code 0.0} if the
     * response did not include a {@code cost} field (the Turnstile response often omits it).
     */
    public double getCost() { return cost; }

    /** Server-reported solve time in milliseconds, or {@code null} if not returned. */
    public Long getSolveTimeMs() { return solveTimeMs; }

    @Override
    public String toString() {
        return "TurnstileResult{" +
                "token='" + (token != null && token.length() > 20
                    ? token.substring(0, 20) + "..."
                    : token) + '\'' +
                ", type='" + type + '\'' +
                ", success=" + success +
                ", cost=" + cost +
                (solveTimeMs != null ? ", solveTimeMs=" + solveTimeMs : "") +
                '}';
    }
}
