package com.nslsolver.models;

import java.util.Objects;

/**
 * Parameters for solving an Akamai Bot Manager challenge.
 *
 * <p>All three fields ({@code url}, {@code userAgent}, {@code proxy}) are
 * required. The returned {@code _abck} cookie is bound to the proxy's
 * egress IP and to the submitted user agent — replay on the same proxy
 * and user agent.
 */
public final class AkamaiParams {

    private final String url;
    private final String userAgent;
    private final String proxy;

    private AkamaiParams(Builder builder) {
        this.url = Objects.requireNonNull(builder.url, "url is required");
        this.userAgent = Objects.requireNonNull(builder.userAgent, "userAgent is required for akamai");
        this.proxy = Objects.requireNonNull(builder.proxy, "proxy is required for akamai");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() { return url; }
    public String getUserAgent() { return userAgent; }
    public String getProxy() { return proxy; }

    @Override
    public String toString() {
        return "AkamaiParams{" +
                "url='" + url + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", proxy='***'" +
                '}';
    }

    public static final class Builder {

        private String url;
        private String userAgent;
        private String proxy;

        private Builder() {}

        /** Required. The Akamai-protected target URL. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Required. Replay the returned cookies with this same user agent. */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /** Required. {@code _abck} is bound to this proxy's egress IP. */
        public Builder proxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        public AkamaiParams build() {
            return new AkamaiParams(this);
        }
    }
}
