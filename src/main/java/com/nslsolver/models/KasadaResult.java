package com.nslsolver.models;

import java.util.Collections;
import java.util.Map;

/** Result of a Kasada solve. Contains headers to include in subsequent requests. */
public final class KasadaResult {

    private final Map<String, String> headers;
    private final String type;
    private final boolean success;

    public KasadaResult(Map<String, String> headers, String type, boolean success) {
        this.headers = headers != null ? Collections.unmodifiableMap(headers) : Collections.emptyMap();
        this.type = type;
        this.success = success;
    }

    public Map<String, String> getHeaders() { return headers; }

    /** Shortcut for the x-kpsdk-ct header. */
    public String getKpsdkCt() { return headers.get("x-kpsdk-ct"); }

    /** Shortcut for the x-kpsdk-cd header. */
    public String getKpsdkCd() { return headers.get("x-kpsdk-cd"); }

    public String getType() { return type; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return "KasadaResult{" +
                "headers=" + headers.keySet() +
                ", type='" + type + '\'' +
                ", success=" + success +
                '}';
    }
}
