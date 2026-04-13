package com.nslsolver.models;

import java.util.Objects;

/** Kasada-specific configuration for the target site's endpoints. */
public final class KasadaConfig {

    private final String pJsPath;
    private final String fpHost;
    private final String tlHost;
    private final String cdConstant;

    private KasadaConfig(Builder builder) {
        this.pJsPath = Objects.requireNonNull(builder.pJsPath, "pJsPath is required");
        this.fpHost = Objects.requireNonNull(builder.fpHost, "fpHost is required");
        this.tlHost = Objects.requireNonNull(builder.tlHost, "tlHost is required");
        this.cdConstant = builder.cdConstant;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPJsPath() { return pJsPath; }
    public String getFpHost() { return fpHost; }
    public String getTlHost() { return tlHost; }
    public String getCdConstant() { return cdConstant; }

    @Override
    public String toString() {
        return "KasadaConfig{" +
                "pJsPath='" + pJsPath + '\'' +
                ", fpHost='" + fpHost + '\'' +
                ", tlHost='" + tlHost + '\'' +
                (cdConstant != null ? ", cdConstant='" + cdConstant + '\'' : "") +
                '}';
    }

    public static final class Builder {

        private String pJsPath;
        private String fpHost;
        private String tlHost;
        private String cdConstant;

        private Builder() {}

        /** Required. Path to the Kasada p.js script. */
        public Builder pJsPath(String pJsPath) {
            this.pJsPath = pJsPath;
            return this;
        }

        /** Required. Fingerprint host. */
        public Builder fpHost(String fpHost) {
            this.fpHost = fpHost;
            return this;
        }

        /** Required. Telemetry host. */
        public Builder tlHost(String tlHost) {
            this.tlHost = tlHost;
            return this;
        }

        /** Optional CD constant override. */
        public Builder cdConstant(String cdConstant) {
            this.cdConstant = cdConstant;
            return this;
        }

        public KasadaConfig build() {
            return new KasadaConfig(this);
        }
    }
}
