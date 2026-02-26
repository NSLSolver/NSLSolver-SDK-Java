package com.nslsolver.models;

import java.util.Objects;

/** Parameters for solving a Cloudflare Challenge page. Proxy is required. */
public final class ChallengeParams {

    private final String url;
    private final String proxy;
    private final String userAgent;

    private ChallengeParams(Builder builder) {
        this.url = Objects.requireNonNull(builder.url, "url is required");
        this.proxy = Objects.requireNonNull(builder.proxy, "proxy is required for challenge solving");
        this.userAgent = builder.userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() { return url; }
    public String getProxy() { return proxy; }
    public String getUserAgent() { return userAgent; }

    @Override
    public String toString() {
        return "ChallengeParams{" +
                "url='" + url + '\'' +
                ", proxy='***'" +
                (userAgent != null ? ", userAgent='" + userAgent + '\'' : "") +
                '}';
    }

    public static final class Builder {

        private String url;
        private String proxy;
        private String userAgent;

        private Builder() {}

        /** Required. The challenge page URL. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Required. Proxy URL, e.g. {@code http://user:pass@host:port}. */
        public Builder proxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        /** Optional user agent override. */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public ChallengeParams build() {
            return new ChallengeParams(this);
        }
    }
}
