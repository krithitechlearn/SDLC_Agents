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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerAdditionalTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnOkResponseWithBody_when_serviceReturnsData() {
        // Arrange
        String accountId = "ACC100";
        String authorizationHeader = "Bearer token-123";
        AccountBalanceResponse balanceResponse = new AccountBalanceResponse();
        balanceResponse.setAccountId(accountId);
        balanceResponse.setCurrency("USD");
        balanceResponse.setCurrentBalance(512.42);
        balanceResponse.setLastUpdated(LocalDateTime.now());

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(balanceResponse);

        // Act
        ResponseEntity<AccountBalanceResponse> actual =
                accountController.getAccountBalance(accountId, authorizationHeader);

        // Assert
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(balanceResponse.getAccountId(), actual.getBody().getAccountId());
        assertEquals(balanceResponse.getCurrentBalance(), actual.getBody().getCurrentBalance());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardNullAuthorizationHeader_when_headerMissing() {
        // Arrange
        String accountId = "ACC-NO-HDR";
        when(accountService.getAccountBalance(eq(accountId), isNull()))
                .thenReturn(new AccountBalanceResponse());

        // Act
        accountController.getAccountBalance(accountId, null);

        // Assert
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(eq(accountId), headerCaptor.capture());
        assertNull(headerCaptor.getValue(), "Controller must pass the header value verbatim (including null)");
    }

    @Test
    void should_propagateRuntimeException_when_serviceThrows() {
        // Arrange
        String accountId = "ACC-ERR";
        String authorizationHeader = "Bearer expired";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new RuntimeException("Unauthorized holder"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Unauthorized holder", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
