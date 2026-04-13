package com.nslsolver.models;

import java.util.Objects;

/** Parameters for solving a Kasada-protected page. */
public final class KasadaParams {

    private final String url;
    private final String userAgent;
    private final int uaVersion;
    private final KasadaConfig kasadaConfig;
    private final String proxy;

    private KasadaParams(Builder builder) {
        this.url = Objects.requireNonNull(builder.url, "url is required");
        this.userAgent = Objects.requireNonNull(builder.userAgent, "userAgent is required");
        if (builder.uaVersion <= 0) {
            throw new IllegalArgumentException("uaVersion must be a positive Chrome major version");
        }
        this.uaVersion = builder.uaVersion;
        this.kasadaConfig = Objects.requireNonNull(builder.kasadaConfig, "kasadaConfig is required");
        this.proxy = builder.proxy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() { return url; }
    public String getUserAgent() { return userAgent; }
    public int getUaVersion() { return uaVersion; }
    public KasadaConfig getKasadaConfig() { return kasadaConfig; }
    public String getProxy() { return proxy; }

    @Override
    public String toString() {
        return "KasadaParams{" +
                "url='" + url + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", uaVersion=" + uaVersion +
                ", kasadaConfig=" + kasadaConfig +
                (proxy != null ? ", proxy='***'" : "") +
                '}';
    }

    public static final class Builder {

        private String url;
        private String userAgent;
        private int uaVersion;
        private KasadaConfig kasadaConfig;
        private String proxy;

        private Builder() {}

        /** Required. The target URL protected by Kasada. */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /** Required. Browser user agent string. */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /** Required. Chrome major version number (e.g. 130). */
        public Builder uaVersion(int uaVersion) {
            this.uaVersion = uaVersion;
            return this;
        }

        /** Required. Kasada endpoint configuration. */
        public Builder kasadaConfig(KasadaConfig kasadaConfig) {
            this.kasadaConfig = kasadaConfig;
            return this;
        }

        /** Optional proxy, e.g. {@code http://user:pass@host:port}. */
        public Builder proxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        public KasadaParams build() {
            return new KasadaParams(this);
        }
    }
}
