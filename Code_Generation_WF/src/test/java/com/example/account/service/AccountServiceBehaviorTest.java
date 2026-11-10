package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountServiceBehaviorTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_stampLastUpdatedWithinInvocationWindow() {
        LocalDateTime before = LocalDateTime.now();

        AccountBalanceResponse response = accountService.getAccountBalance("ACC_TIME", "Bearer token");

        LocalDateTime after = LocalDateTime.now();
        assertNotNull(response.getLastUpdated(), "Timestamp required for audit traceability");
        assertTrue(!response.getLastUpdated().isBefore(before),
                "lastUpdated should not precede invocation start");
        assertTrue(!response.getLastUpdated().isAfter(after),
                "lastUpdated should be captured during invocation window");
    }

    @Test
    void should_echoProvidedAccountIdEvenWhenBlank() {
        AccountBalanceResponse response = accountService.getAccountBalance("", "Bearer token");

        assertEquals("", response.getAccountId(), "Account identifier must be echoed for traceability");
        assertEquals("USD", response.getCurrency());
        assertEquals(1000.00, response.getCurrentBalance());
    }

    @Test
    void should_createNewResponseObjectPerInvocation() {
        AccountBalanceResponse first = accountService.getAccountBalance("ACC1", "Bearer a");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC1", "Bearer a");

        assertNotSame(first, second, "Service must not reuse DTO instances across calls");
    }
}
