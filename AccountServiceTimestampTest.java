package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountServiceTimestampTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_setLastUpdatedWithinInvocationWindow() {
        LocalDateTime beforeCall = LocalDateTime.now();
        AccountBalanceResponse response = accountService.getAccountBalance("ACC_TIME", "Bearer any");
        LocalDateTime afterCall = LocalDateTime.now();

        assertNotNull(response.getLastUpdated(), "Service must stamp lastUpdated for auditing requirements");
        assertFalse(response.getLastUpdated().isBefore(beforeCall),
                "lastUpdated should not precede the invocation time");
        assertFalse(response.getLastUpdated().isAfter(afterCall),
                "lastUpdated should be captured as part of the same invocation window");
    }

    @Test
    void should_preserveAccountIdEvenWhenInputIsBlank() {
        AccountBalanceResponse response = accountService.getAccountBalance("", "Bearer irrelevant");

        assertEquals("", response.getAccountId(), "Service must echo the requested account id for traceability");
        assertEquals("USD", response.getCurrency());
    }

    @Test
    void should_returnDefaultBalanceRegardlessOfAuthorizationHeaderFormat() {
        AccountBalanceResponse response = accountService.getAccountBalance("ACC010", "malformed header");

        assertEquals(1000.00, response.getCurrentBalance(), 0.0001);
        assertEquals("USD", response.getCurrency());
    }
}
