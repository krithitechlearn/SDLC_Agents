package com.example.account;

import com.example.account.dto.AccountBalanceResponse;
import com.example.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnBalance_when_accountExists() {
        String accountId = "ACC123";
        String authorizationHeader = "Bearer mock";
        AccountBalanceResponse expectedResponse = new AccountBalanceResponse();
        expectedResponse.setAccountId(accountId);
        expectedResponse.setCurrentBalance(1250.50);
        expectedResponse.setCurrency("USD");

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(expectedResponse);

        ResponseEntity<AccountBalanceResponse> actualResponse =
                accountController.getAccountBalance(accountId, authorizationHeader);

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_throwException_when_serviceFails() {
        String accountId = "ACC999";
        String authorizationHeader = "Bearer invalid";

        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new IllegalStateException("Service unavailable"));

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Service unavailable", thrown.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
