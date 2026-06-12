package com.nslsolver.models;

import java.util.Objects;

/** Parameters for solving a Cloudflare Turnstile captcha. */
public final class TurnstileParams {

    private final String siteKey;
    private final String url;
    private final String action;
    private final String cdata;
    private final String proxy;
    private final String userAgent;

    private TurnstileParams(Builder builder) {
        this.siteKey = Objects.requireNonNull(builder.siteKey, "siteKey is required");
        this.url = Objects.requireNonNull(builder.url, "url is required");
        this.action = builder.action;
        this.cdata = builder.cdata;
        this.proxy = builder.proxy;
        this.userAgent = builder.userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSiteKey() { return siteKey; }
    public String getUrl() { return url; }
    public String getAction() { return action; }
    public String getCdata() { return cdata; }
    public String getProxy() { return proxy; }
    public String getUserAgent() { return userAgent; }

    @Override
    public String toString() {
        return "TurnstileParams{" +
                "siteKey='" + siteKey + '\'' +
                ", url='" + url + '\'' +
                (action != null ? ", action='" + action + '\'' : "") +
                (cdata != null ? ", cdata='" + cdata + '\'' : "") +
                (proxy != null ? ", proxy='***'" : "") +
                (userAgent != null ? ", userAgent='" + userAgent + '\'' : "") +
                '}';
    }

    public static final class Builder {

        private String siteKey;
        private String url;
        private String action;
        private String cdata;
        private String proxy;
        private String userAgent;

        private Builder() {}

        /** Required. The Turnstile site key from the target page. */
        public Builder siteKey(String siteKey) {
            this.siteKey = siteKey;
            return this;
        }

        /** Required. Page URL where the widget is rendered. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Optional action parameter for the Turnstile widget. */
        public Builder action(String action) {
            this.action = action;
            return this;
        }

        /** Optional custom data for the Turnstile widget. */
        public Builder cdata(String cdata) {
            this.cdata = cdata;
            return this;
        }

        /**
         * Advanced/optional. Proxy, e.g. {@code http://user:pass@host:port}. Not part of the
         * documented Turnstile parameter set (site_key/url/action/cdata); only sent when set.
         */
        public Builder proxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Advanced/optional. User agent override. Not part of the documented Turnstile parameter
         * set (site_key/url/action/cdata); only sent when set.
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public TurnstileParams build() {
            return new TurnstileParams(this);
        }
    }
}
