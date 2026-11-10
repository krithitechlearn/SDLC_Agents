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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerEdgeCaseTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnOkResponseWithServicePayload_when_balanceAvailable() {
        // Arrange
        String accountId = "ACC-EDGE-001";
        String authorizationHeader = "Bearer scope-account.balance.read";
        AccountBalanceResponse serviceResponse = new AccountBalanceResponse();
        serviceResponse.setAccountId(accountId);
        serviceResponse.setCurrentBalance(875.32);
        serviceResponse.setCurrency("USD");
        serviceResponse.setLastUpdated(LocalDateTime.now());

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(serviceResponse);

        // Act
        ResponseEntity<AccountBalanceResponse> response =
                accountController.getAccountBalance(accountId, authorizationHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(serviceResponse, response.getBody(), "Controller must return the service payload unchanged");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardWhitespaceAuthorizationHeaderVerbatim() {
        // Arrange
        String accountId = "ACC-WHITESPACE";
        String authorizationHeader = "Bearer    multi   space";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenReturn(new AccountBalanceResponse());

        // Act
        accountController.getAccountBalance(accountId, authorizationHeader);

        // Assert
        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());

        assertEquals(accountId, accountCaptor.getValue(), "Account identifier must be forwarded without mutation");
        assertEquals(authorizationHeader, headerCaptor.getValue(),
                "Authorization header should be propagated verbatim to satisfy audit traceability");
    }

    @Test
    void should_propagateIllegalArgumentException_when_serviceRejectsAuthorization() {
        // Arrange
        String accountId = "ACC-UNAUTHORIZED";
        String authorizationHeader = "Bearer invalid-scope";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new IllegalArgumentException("Authorization failed"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Authorization failed", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
