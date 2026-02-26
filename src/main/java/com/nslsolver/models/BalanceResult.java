package com.nslsolver.models;

import java.util.Collections;
import java.util.List;

/** Account balance, thread limit, and allowed captcha types. */
public final class BalanceResult {

    private final double balance;
    private final int maxThreads;
    private final List<String> allowedTypes;

    public BalanceResult(double balance, int maxThreads, List<String> allowedTypes) {
        this.balance = balance;
        this.maxThreads = maxThreads;
        this.allowedTypes = allowedTypes != null
                ? Collections.unmodifiableList(allowedTypes)
                : Collections.emptyList();
    }

    public double getBalance() { return balance; }
    public int getMaxThreads() { return maxThreads; }
    public List<String> getAllowedTypes() { return allowedTypes; }

    @Override
    public String toString() {
        return "BalanceResult{" +
                "balance=" + balance +
                ", maxThreads=" + maxThreads +
                ", allowedTypes=" + allowedTypes +
                '}';
    }
}
