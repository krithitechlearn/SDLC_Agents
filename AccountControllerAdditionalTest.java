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
    void should_returnOkWithNullBody_when_serviceReturnsNull() {
        String accountId = "ACC-NULL";
        String authorizationHeader = "Bearer token";
        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(null);

        ResponseEntity<AccountBalanceResponse> response =
                accountController.getAccountBalance(accountId, authorizationHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Controller must keep HTTP 200 for successful flow");
        assertNull(response.getBody(), "Body should mirror the service result even when null");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardArgumentsVerbatim_when_valuesContainWhitespace() {
        String accountId = "  ACC-123  ";
        String authorizationHeader = "Bearer    spaced";
        AccountBalanceResponse serviceResponse = new AccountBalanceResponse();
        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(serviceResponse);

        accountController.getAccountBalance(accountId, authorizationHeader);

        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());

        assertEquals(accountId, accountCaptor.getValue(), "Account identifier must not be trimmed or mutated");
        assertEquals(authorizationHeader, headerCaptor.getValue(), "Authorization header must be forwarded verbatim");
    }

    @Test
    void should_forwardNullAuthorizationHeader_when_headerMissing() {
        String accountId = "ACC-NO-HDR";
        when(accountService.getAccountBalance(eq(accountId), isNull())).thenReturn(new AccountBalanceResponse());

        accountController.getAccountBalance(accountId, null);

        verify(accountService).getAccountBalance(eq(accountId), isNull());
    }

    @Test
    void should_propagateRuntimeException_when_serviceThrows() {
        String accountId = "ACC-ERR";
        String authorizationHeader = "Bearer expired";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new RuntimeException("Unauthorized holder"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Unauthorized holder", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
