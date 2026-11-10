package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class AccountServiceBehaviorTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_stampTimestampWithinInvocationWindow() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-TIME", "Bearer anything");

        // Assert
        LocalDateTime afterCall = LocalDateTime.now();
        assertNotNull(response.getLastUpdated(), "lastUpdated is required for audit traceability");
        assertFalse(response.getLastUpdated().isBefore(beforeCall),
                "Timestamp should not precede the invocation");
        assertFalse(response.getLastUpdated().isAfter(afterCall),
                "Timestamp should be captured within the invocation window");
    }

    @Test
    void should_createNewResponseInstanceForEachCall() {
        // Act
        AccountBalanceResponse first = accountService.getAccountBalance("ACC-1", "Bearer x");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC-1", "Bearer x");

        // Assert
        assertNotSame(first, second, "Service should create a fresh DTO per call to avoid shared state");
    }

    @Test
    void should_preserveProvidedAccountIdEvenIfBlank() {
        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("", "Bearer token");

        // Assert
        assertEquals("", response.getAccountId(), "Account identifier must be echoed back even if blank");
    }

    @Test
    void should_defaultBalanceAndCurrencyRegardlessOfAuthorizationHeader() {
        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-DEFAULT", "malformed header");

        // Assert
        assertEquals(1000.00, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
    }
}
