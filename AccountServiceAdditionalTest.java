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
                "Timestamp must not precede the invocation start");
        assertTrue(!response.getLastUpdated().isAfter(afterCall),
                "Timestamp must be captured within the invocation window");
    }

    @Test
    void should_preserveProvidedAccountIdFormatting() {
        String accountId = "  ACC-777  ";

        AccountBalanceResponse response = accountService.getAccountBalance(accountId, "Bearer any");

        assertEquals(accountId, response.getAccountId(), "Service must echo the provided identifier without trimming");
    }

    @Test
    void should_createDistinctResponseInstancesPerCall() {
        AccountBalanceResponse first = accountService.getAccountBalance("ACC-001", "Bearer one");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC-001", "Bearer one");

        assertNotSame(first, second, "Each invocation should return a new DTO instance to avoid shared state");
    }

    @Test
    void should_returnDefaultBalanceRegardlessOfAuthorizationHeader() {
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-DEFAULT", "malformed header");

        assertEquals(1000.00, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
    }
}
