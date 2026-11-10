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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerNullResponseTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    void should_returnOkWithEmptyBody_when_serviceReturnsNull() {
        String accountId = "ACC0001";
        String authorizationHeader = "Bearer valid";

        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(null);

        ResponseEntity<AccountBalanceResponse> response = accountController.getAccountBalance(accountId, authorizationHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Controller must still respond with HTTP 200 on success path");
        assertNull(response.getBody(), "Body should reflect the service result when it is null");
        verify(accountService).getAccountBalance(accountId, authorizationHeader);
    }

    @Test
    void should_forwardAuthorizationHeaderVerbatim_when_headerContainsAdditionalWhitespace() {
        String accountId = "ACC-TRIM";
        String authorizationHeader = "Bearer    spaced";
        AccountBalanceResponse response = new AccountBalanceResponse();
        when(accountService.getAccountBalance(accountId, authorizationHeader)).thenReturn(response);

        accountController.getAccountBalance(accountId, authorizationHeader);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountService).getAccountBalance(org.mockito.ArgumentMatchers.eq(accountId), headerCaptor.capture());

        assertEquals(authorizationHeader, headerCaptor.getValue(), "Authorization header should be passed without trimming or mutation");
    }
}
