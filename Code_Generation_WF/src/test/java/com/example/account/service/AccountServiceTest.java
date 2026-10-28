package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void should_populateResponseWithFixedValues() {
        String accountId = "ACC12345";
        String authorizationHeader = "Bearer stub";

        AccountBalanceResponse response = accountService.getAccountBalance(accountId, authorizationHeader);

        assertEquals(accountId, response.getAccountId());
        assertEquals(1000.00, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getLastUpdated());
    }

    @Test
    void should_handleNullInputsWithoutExceptions() {
        AccountBalanceResponse response = assertDoesNotThrow(() -> accountService.getAccountBalance(null, null));

        assertNull(response.getAccountId());
        assertEquals(1000.00, response.getCurrentBalance());
        assertEquals("USD", response.getCurrency());
        assertNotNull(response.getLastUpdated());
    }
}
