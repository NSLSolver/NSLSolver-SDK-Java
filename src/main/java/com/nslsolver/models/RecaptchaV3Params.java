package com.nslsolver.models;

import java.util.Objects;

/**
 * Parameters for solving a reCAPTCHA v3 (incl. Enterprise) challenge.
 *
 * <p>{@code siteKey}, {@code url}, and {@code proxy} are required. The score-based
 * token is bound to the supplied {@code action}, so it must match the action the
 * target page expects (the server defaults to {@code verify} when none is given).
 * Set {@code enterprise} when the site uses reCAPTCHA Enterprise.
 */
public final class RecaptchaV3Params {

    private final String siteKey;
    private final String url;
    private final String proxy;
    private final String action;
    private final boolean enterprise;
    private final String userAgent;

    private RecaptchaV3Params(Builder builder) {
        this.siteKey = Objects.requireNonNull(builder.siteKey, "siteKey is required");
        this.url = Objects.requireNonNull(builder.url, "url is required");
        this.proxy = Objects.requireNonNull(builder.proxy, "proxy is required for recaptchav3");
        this.action = builder.action;
        this.enterprise = builder.enterprise;
        this.userAgent = builder.userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSiteKey() { return siteKey; }
    public String getUrl() { return url; }
    public String getProxy() { return proxy; }

    /** Optional action. When {@code null} the server defaults to {@code verify}. */
    public String getAction() { return action; }

    /** True to solve via reCAPTCHA Enterprise. */
    public boolean isEnterprise() { return enterprise; }

    public String getUserAgent() { return userAgent; }

    @Override
    public String toString() {
        return "RecaptchaV3Params{" +
                "siteKey='" + siteKey + '\'' +
                ", url='" + url + '\'' +
                ", proxy='***'" +
                (action != null ? ", action='" + action + '\'' : "") +
                ", enterprise=" + enterprise +
                (userAgent != null ? ", userAgent='" + userAgent + '\'' : "") +
                '}';
    }

    public static final class Builder {

        private String siteKey;
        private String url;
        private String proxy;
        private String action;
        private boolean enterprise;
        private String userAgent;

        private Builder() {}

        /** Required. The reCAPTCHA site key from the target page. */
        public Builder siteKey(String siteKey) {
            this.siteKey = siteKey;
            return this;
        }

        /** Required. Page URL where the reCAPTCHA is rendered. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Required. Proxy URL, e.g. {@code http://user:pass@host:port}. */
        public Builder proxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        /** Optional action. Defaults to {@code verify} server-side when not set. */
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        /** Set when the target uses reCAPTCHA Enterprise. Defaults to {@code false}. */
        public Builder enterprise(boolean enterprise) {
            this.enterprise = enterprise;
            return this;
        }

        /** Optional user agent override. */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public RecaptchaV3Params build() {
            return new RecaptchaV3Params(this);
        }
    }
}
