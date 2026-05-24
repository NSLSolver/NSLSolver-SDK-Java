package com.nslsolver.models;

import java.util.Collections;
import java.util.List;

/** Account balance, plan flags, allowed captcha types, and live CPM (captchas-per-minute) usage. */
public final class BalanceResult {

    private final double balance;
    private final boolean unlimited;
    private final List<String> allowedTypes;
    private final int maxCpm;
    private final int currentCpm;
    private final int cpmLimit;
    private final String unlimitedExpiresAt;

    public BalanceResult(
            double balance,
            boolean unlimited,
            List<String> allowedTypes,
            int maxCpm,
            int currentCpm,
            int cpmLimit,
            String unlimitedExpiresAt) {
        this.balance = balance;
        this.unlimited = unlimited;
        this.allowedTypes = allowedTypes != null
                ? Collections.unmodifiableList(allowedTypes)
                : Collections.emptyList();
        this.maxCpm = maxCpm;
        this.currentCpm = currentCpm;
        this.cpmLimit = cpmLimit;
        this.unlimitedExpiresAt = unlimitedExpiresAt;
    }

    public double getBalance() { return balance; }
    public boolean isUnlimited() { return unlimited; }
    public List<String> getAllowedTypes() { return allowedTypes; }

    /** Per-key captchas-per-minute ceiling. 0 means uncapped. */
    public int getMaxCpm() { return maxCpm; }

    /** Tokens consumed in the rolling CPM window. */
    public int getCurrentCpm() { return currentCpm; }

    /** Mirror of {@link #getMaxCpm()} for dashboards. */
    public int getCpmLimit() { return cpmLimit; }

    /** ISO 8601 timestamp when an unlimited plan expires, or {@code null}. */
    public String getUnlimitedExpiresAt() { return unlimitedExpiresAt; }

    @Override
    public String toString() {
        return "BalanceResult{" +
                "balance=" + balance +
                ", unlimited=" + unlimited +
                ", allowedTypes=" + allowedTypes +
                ", maxCpm=" + maxCpm +
                ", currentCpm=" + currentCpm +
                ", cpmLimit=" + cpmLimit +
                ", unlimitedExpiresAt='" + unlimitedExpiresAt + '\'' +
                '}';
    }
}
