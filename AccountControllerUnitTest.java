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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerUnitTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnOkResponse_when_balanceRetrievedSuccessfully() {
        // Arrange
        String accountId = "ACC-1001";
        String authorizationHeader = "Bearer mock-token";
        AccountBalanceResponse serviceResponse = new AccountBalanceResponse();
        serviceResponse.setAccountId(accountId);
        serviceResponse.setCurrentBalance(2450.75);
        serviceResponse.setCurrency("USD");

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(serviceResponse);

        // Act
        ResponseEntity<AccountBalanceResponse> response =
                accountController.getAccountBalance(accountId, authorizationHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody(), "Controller should return the response from the service as-is");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_passAccountIdAndAuthorizationHeader_toService() {
        // Arrange
        String accountId = "ACC-ARG";
        String authorizationHeader = "Bearer scope-account.balance.read";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenReturn(new AccountBalanceResponse());

        // Act
        accountController.getAccountBalance(accountId, authorizationHeader);

        // Assert
        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());

        assertEquals(accountId, accountCaptor.getValue(), "Account identifier must be forwarded without modification");
        assertEquals(authorizationHeader, headerCaptor.getValue(), "Authorization header must be forwarded verbatim");
    }

    @Test
    void should_propagateException_when_serviceThrows() {
        // Arrange
        String accountId = "ACC-ERR";
        String authorizationHeader = "Bearer invalid";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new IllegalStateException("Service unavailable"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Service unavailable", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
