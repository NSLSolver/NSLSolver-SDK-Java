package com.nslsolver.models;

import java.util.Collections;
import java.util.Map;

/** Result of a Challenge solve. Contains cookies (including cf_clearance) and the user agent to reuse. */
public final class ChallengeResult {

    private final Map<String, String> cookies;
    private final String userAgent;
    private final String type;
    private final boolean success;

    public ChallengeResult(Map<String, String> cookies, String userAgent, String type, boolean success) {
        this.cookies = cookies != null ? Collections.unmodifiableMap(cookies) : Collections.emptyMap();
        this.userAgent = userAgent;
        this.type = type;
        this.success = success;
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

    @Override
    public String toString() {
        return "ChallengeResult{" +
                "cookies=" + cookies.keySet() +
                ", userAgent='" + userAgent + '\'' +
                ", type='" + type + '\'' +
                ", success=" + success +
                '}';
    }
}
