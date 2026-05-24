package com.nslsolver.models;

import java.util.Collections;
import java.util.Map;

/**
 * Result of an Akamai Bot Manager solve. Contains the cookie jar — most
 * importantly {@code _abck} — to replay on the protected origin paired
 * with the same user agent and proxy/exit IP submitted with the solve.
 */
public final class AkamaiResult {

    private final Map<String, String> cookies;
    private final String type;
    private final boolean success;
    private final double cost;

    public AkamaiResult(Map<String, String> cookies, String type, boolean success, double cost) {
        this.cookies = cookies != null ? Collections.unmodifiableMap(cookies) : Collections.emptyMap();
        this.type = type;
        this.success = success;
        this.cost = cost;
    }

    public Map<String, String> getCookies() { return cookies; }

    /** Shortcut for the {@code _abck} cookie value. */
    public String getAbck() { return cookies.get("_abck"); }

    /** Shortcut for the {@code bm_sz} cookie value. */
    public String getBmSz() { return cookies.get("bm_sz"); }

    public String getType() { return type; }
    public boolean isSuccess() { return success; }

    /** USD deducted from the account balance for this solve. */
    public double getCost() { return cost; }

    @Override
    public String toString() {
        return "AkamaiResult{" +
                "cookies=" + cookies.keySet() +
                ", type='" + type + '\'' +
                ", success=" + success +
                ", cost=" + cost +
                '}';
    }
}
