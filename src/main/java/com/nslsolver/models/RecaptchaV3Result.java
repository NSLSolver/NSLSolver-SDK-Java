package com.nslsolver.models;

/**
 * Result of a reCAPTCHA v3 solve. The {@code token} is the g-recaptcha-response
 * value to submit to the target site.
 *
 * <p>Note: the {@code type} echoed back by the API is the hyphenated slug
 * {@code recaptcha-v3} (not the bare {@code recaptchav3} request discriminator),
 * and may be {@code null} if the response omits it.
 */
public final class RecaptchaV3Result {

    private final String token;
    private final String action;
    private final String type;
    private final boolean success;

    public RecaptchaV3Result(String token, String action, String type, boolean success) {
        this.token = token;
        this.action = action;
        this.type = type;
        this.success = success;
    }

    /** The reCAPTCHA token to submit to the target site. */
    public String getToken() { return token; }

    /** The action the token was issued for. May be {@code null} if not echoed back. */
    public String getAction() { return action; }

    /** The response type slug, typically {@code recaptcha-v3}. May be {@code null}. */
    public String getType() { return type; }

    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return "RecaptchaV3Result{" +
                "token='" + (token != null && token.length() > 20
                    ? token.substring(0, 20) + "..."
                    : token) + '\'' +
                (action != null ? ", action='" + action + '\'' : "") +
                ", type='" + type + '\'' +
                ", success=" + success +
                '}';
    }
}
