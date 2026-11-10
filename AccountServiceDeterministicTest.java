package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class AccountServiceDeterministicTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_createNewResponseInstance_forEveryInvocation() {
        AccountBalanceResponse first = accountService.getAccountBalance("ACC111", "Bearer one");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC111", "Bearer one");

        assertNotSame(first, second, "Service should not reuse DTO instances between invocations");
        assertNotNull(first.getLastUpdated(), "First response should contain lastUpdated timestamp");
        assertNotNull(second.getLastUpdated(), "Second response should contain lastUpdated timestamp");
    }

    @Test
    void should_preserveIncomingAccountIdFormatting() {
        String accountId = "  ACC-777  ";

        AccountBalanceResponse response = accountService.getAccountBalance(accountId, "Bearer any");

        assertEquals(accountId, response.getAccountId(), "Service should not trim or transform the provided account identifier");
    }

    @Test
    void should_handleVeryLongAccountIdentifiers() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 512; i++) {
            builder.append('A');
        }
        String longAccountId = builder.toString();

        AccountBalanceResponse response = accountService.getAccountBalance(longAccountId, "Bearer long");

        assertEquals(longAccountId, response.getAccountId(), "Large account identifiers must be echoed back for traceability");
        assertEquals("USD", response.getCurrency(), "Currency should remain the configured default");
    }
}
