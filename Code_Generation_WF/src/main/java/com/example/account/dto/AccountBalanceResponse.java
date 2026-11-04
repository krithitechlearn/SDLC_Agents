package com.example.account.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class AccountBalanceResponse {

    private String accountId;
    private double currentBalance;
    private String currency;
    private LocalDateTime lastUpdated;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountBalanceResponse that = (AccountBalanceResponse) o;
        return Double.compare(that.currentBalance, currentBalance) == 0
                && Objects.equals(accountId, that.accountId)
                && Objects.equals(currency, that.currency)
                && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, currentBalance, currency, lastUpdated);
    }

    @Override
    public String toString() {
        return "AccountBalanceResponse{"
                + "accountId='" + accountId + '\''
                + ", currentBalance=" + currentBalance
                + ", currency='" + currency + '\''
                + ", lastUpdated=" + lastUpdated
                + '}';
    }
}
