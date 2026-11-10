package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountServiceAuditTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_stampLastUpdatedWithinInvocationWindow() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-AUDIT", "Bearer valid");

        // Assert
        LocalDateTime afterCall = LocalDateTime.now();
        assertNotNull(response.getLastUpdated(), "lastUpdated is mandatory for audit traceability as per LLD");
        assertTrue(!response.getLastUpdated().isBefore(beforeCall),
                "Timestamp must not precede the method invocation window");
        assertTrue(!response.getLastUpdated().isAfter(afterCall),
                "Timestamp must be captured during the same invocation window");
    }

    @Test
    void should_createDistinctResponseInstancesPerCall() {
        // Act
        AccountBalanceResponse first = accountService.getAccountBalance("ACC-INSTANCE", "Bearer one");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC-INSTANCE", "Bearer one");

        // Assert
        assertNotSame(first, second, "Service must return a fresh DTO each time to avoid stale data exposure");
    }

    @Test
    void should_preserveVeryLongAccountIdentifiers() {
        // Arrange
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            builder.append('9');
        }
        String longAccountId = builder.toString();

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance(longAccountId, "Bearer token");

        // Assert
        assertEquals(longAccountId, response.getAccountId(),
                "Service must echo the requested account for downstream reconciliation");
        assertEquals("USD", response.getCurrency());
        assertEquals(1000.00, response.getCurrentBalance());
    }

    @Test
    void should_defaultCurrencyToUsd_when_authorizationHeaderMissing() {
        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-NO-HDR", null);

        // Assert
        assertEquals("USD", response.getCurrency(), "Currency default should comply with API contract");
        assertEquals(1000.00, response.getCurrentBalance());
    }
}
