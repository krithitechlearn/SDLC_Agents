package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountServiceAdditionalTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_stampLastUpdatedWithinInvocationWindow() {
        LocalDateTime beforeCall = LocalDateTime.now();

        AccountBalanceResponse response = accountService.getAccountBalance("ACC-TIME", "Bearer token");

        LocalDateTime afterCall = LocalDateTime.now();
        assertNotNull(response.getLastUpdated(), "lastUpdated is required for audit traceability");
        assertTrue(!response.getLastUpdated().isBefore(beforeCall),
                "Timestamp must not precede the invocation time");
        assertTrue(!response.getLastUpdated().isAfter(afterCall),
                "Timestamp should be captured during the same invocation window");
    }

    @Test
    void should_preserveAccountIdEvenWhenBlank() {
        AccountBalanceResponse response = accountService.getAccountBalance("", "Bearer anything");

        assertEquals("", response.getAccountId(), "Service should echo the provided account identifier");
        assertEquals("USD", response.getCurrency(), "Currency should remain the configured default");
    }

    @Test
    void should_createDistinctResponseInstancesPerCall() {
        AccountBalanceResponse first = accountService.getAccountBalance("ACC-001", "Bearer one");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC-001", "Bearer one");

        assertNotSame(first, second, "Each invocation must return a new DTO instance to avoid shared mutable state");
    }
}
