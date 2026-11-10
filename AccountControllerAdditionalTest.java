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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Controller should keep HTTP 200 on successful service call");
        assertNull(response.getBody(), "Body must mirror the service result even when it is null");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardArgumentsVerbatim_when_valuesContainWhitespace() {
        String accountId = "  ACC-123  ";
        String authorizationHeader = "Bearer    spaced";
        AccountBalanceResponse response = new AccountBalanceResponse();
        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(response);

        accountController.getAccountBalance(accountId, authorizationHeader);

        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());

        assertSame(accountId, accountCaptor.getValue(), "Account identifier must not be trimmed or altered");
        assertSame(authorizationHeader, headerCaptor.getValue(), "Authorization header should be forwarded verbatim");
    }

    @Test
    void should_propagateIllegalArgumentException_when_serviceRejectsRequest() {
        when(accountService.getAccountBalance(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Unauthorized holder"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountController.getAccountBalance("ACC-401", "Bearer expired"));

        assertEquals("Unauthorized holder", exception.getMessage());
        verify(accountService, times(1)).getAccountBalance("ACC-401", "Bearer expired");
    }
}
