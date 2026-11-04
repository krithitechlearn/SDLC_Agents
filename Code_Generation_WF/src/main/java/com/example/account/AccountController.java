package com.example.account;

import com.example.account.dto.AccountBalanceResponse;
import com.example.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @PathVariable("accountId") String accountId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (!StringUtils.hasText(accountId)) {
            throw new IllegalArgumentException("accountId is required");
        }

        AccountBalanceResponse accountBalance = accountService.getAccountBalance(accountId, authorizationHeader);
        return ResponseEntity.ok(accountBalance);
    }
}
