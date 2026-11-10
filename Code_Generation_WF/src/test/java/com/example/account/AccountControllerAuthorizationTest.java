package com.example.account;

import com.example.account.dto.AccountBalanceResponse;
import com.example.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerAuthorizationTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnHttpOkWithResponseBody_when_balanceRetrieved() {
        String accountId = "ACC123";
        String authorizationHeader = "Bearer scope";
        AccountBalanceResponse serviceResponse = new AccountBalanceResponse();
        serviceResponse.setAccountId(accountId);
        serviceResponse.setCurrentBalance(250.25);
        serviceResponse.setCurrency("USD");

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(serviceResponse);

        ResponseEntity<AccountBalanceResponse> response =
                accountController.getAccountBalance(accountId, authorizationHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody(), "Controller must expose the service payload as-is");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardNullAuthorizationHeader_when_headerMissing() {
        String accountId = "ACC_NULL";
        AccountBalanceResponse serviceResponse = new AccountBalanceResponse();
        when(accountService.getAccountBalance(accountId, null)).thenReturn(serviceResponse);

        accountController.getAccountBalance(accountId, null);

        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());
        assertEquals(accountId, accountCaptor.getValue());
        assertNull(headerCaptor.getValue(), "Controller must propagate null headers without mutation");
    }

    @Test
    void should_propagateIllegalStateException_when_serviceFails() {
        String accountId = "ACC_ERR";
        String authorizationHeader = "Bearer invalid";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new IllegalStateException("Service unavailable"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Service unavailable", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
