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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerAuthorizationTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnHttpOkWithPayload_when_authorizedScopeIsPresent() {
        String accountId = "ACC123";
        String authorizationHeader = "Bearer scope";
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setAccountId(accountId);
        response.setCurrentBalance(512.78);
        response.setCurrency("USD");
        response.setLastUpdated(LocalDateTime.now());

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(response);

        ResponseEntity<AccountBalanceResponse> actual = accountController.getAccountBalance(accountId, authorizationHeader);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertSame(response, actual.getBody(), "Controller should return the exact response from the service");
        assertEquals("USD", actual.getBody().getCurrency());
        assertTrue(actual.getBody().getLastUpdated().isBefore(LocalDateTime.now().plusSeconds(1)));
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardNullAuthorizationHeader_when_headerMissing() {
        String accountId = "ACC-NULL";
        AccountBalanceResponse response = new AccountBalanceResponse();
        when(accountService.getAccountBalance(eq(accountId), isNull())).thenReturn(response);

        accountController.getAccountBalance(accountId, null);

        ArgumentCaptor<String> accountCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(accountCaptor.capture(), headerCaptor.capture());

        assertEquals(accountId, accountCaptor.getValue(), "Controller must pass the received accountId to the service");
        assertEquals(null, headerCaptor.getValue(), "Authorization header should be forwarded even when missing");
    }

    @Test
    void should_propagateIllegalArgumentException_when_serviceRejectsRequest() {
        String accountId = "ACC401";
        String authorizationHeader = "Bearer expired";
        when(accountService.getAccountBalance(accountId, authorizationHeader))
                .thenThrow(new IllegalArgumentException("Unauthorized holder"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountController.getAccountBalance(accountId, authorizationHeader));

        assertEquals("Unauthorized holder", exception.getMessage());
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }
}
