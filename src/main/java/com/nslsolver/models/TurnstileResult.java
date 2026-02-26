package com.nslsolver.models;

/** Result of a Turnstile solve. The token goes in the {@code cf-turnstile-response} form field. */
public final class TurnstileResult {

    private final String token;
    private final String type;
    private final boolean success;

    public TurnstileResult(String token, String type, boolean success) {
        this.token = token;
        this.type = type;
        this.success = success;
    }

    public String getToken() { return token; }
    public String getType() { return type; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return "TurnstileResult{" +
                "token='" + (token != null && token.length() > 20
                    ? token.substring(0, 20) + "..."
                    : token) + '\'' +
                ", type='" + type + '\'' +
                ", success=" + success +
                '}';
    }
}
