package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountServiceUnitTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_returnDefaultBalanceAndCurrency_when_accountIdProvided() {
        // Arrange
        String accountId = "ACC-DEFAULT";

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance(accountId, "Bearer token");

        // Assert
        assertEquals(accountId, response.getAccountId());
        assertEquals(1000.0, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getLastUpdated());
    }

    @Test
    void should_setNullAccountId_when_inputIsNull() {
        // Act
        AccountBalanceResponse response = accountService.getAccountBalance(null, null);

        // Assert
        assertNull(response.getAccountId());
        assertEquals(1000.0, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getLastUpdated());
    }

    @Test
    void should_stampTimestampWithinInvocationWindow() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        AccountBalanceResponse response = accountService.getAccountBalance("ACC-TIME", "Bearer token");

        // Assert
        LocalDateTime afterCall = LocalDateTime.now();
        assertNotNull(response.getLastUpdated(), "lastUpdated timestamp is required for audit traceability");
        assertTrue(!response.getLastUpdated().isBefore(beforeCall),
                "Timestamp should not be earlier than the method invocation");
        assertTrue(!response.getLastUpdated().isAfter(afterCall),
                "Timestamp should be captured within the invocation window");
    }

    @Test
    void should_createDistinctResponseInstances_perInvocation() {
        // Act
        AccountBalanceResponse first = accountService.getAccountBalance("ACC-1", "Bearer token");
        AccountBalanceResponse second = accountService.getAccountBalance("ACC-1", "Bearer token");

        // Assert
        assertNotSame(first, second, "Service should create a new DTO for each invocation");
    }
}
