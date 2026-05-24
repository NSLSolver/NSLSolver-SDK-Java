package com.nslsolver.models;

import java.util.Collections;
import java.util.Map;

/** Result of a Challenge solve. Contains cookies (including cf_clearance) and the user agent to reuse. */
public final class ChallengeResult {

    private final Map<String, String> cookies;
    private final String userAgent;
    private final String type;
    private final boolean success;
    private final String token;
    private final double cost;

    public ChallengeResult(
            Map<String, String> cookies,
            String userAgent,
            String type,
            boolean success,
            String token,
            double cost) {
        this.cookies = cookies != null ? Collections.unmodifiableMap(cookies) : Collections.emptyMap();
        this.userAgent = userAgent;
        this.type = type;
        this.success = success;
        this.token = token;
        this.cost = cost;
    }

    public Map<String, String> getCookies() { return cookies; }

    /** Shortcut for {@code getCookies().get("cf_clearance")}. */
    public String getCfClearance() {
        return cookies.get("cf_clearance");
    }

    /** The user agent used during the solve -- you must reuse it for subsequent requests. */
    public String getUserAgent() { return userAgent; }
    public String getType() { return type; }
    public boolean isSuccess() { return success; }

    /** Set when the challenge page returned a Turnstile-style token instead of cookies. May be {@code null}. */
    public String getToken() { return token; }

    /** USD deducted from the account balance for this solve. */
    public double getCost() { return cost; }

    @Override
    public String toString() {
        return "ChallengeResult{" +
                "cookies=" + cookies.keySet() +
                ", userAgent='" + userAgent + '\'' +
                ", type='" + type + '\'' +
                ", success=" + success +
                ", cost=" + cost +
                '}';
    }
}
