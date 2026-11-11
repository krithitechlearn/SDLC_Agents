package com.example.account;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @PathVariable String accountId,
            @RequestHeader("Authorization") String authorizationHeader) {
        AccountBalanceResponse response = accountService.getAccountBalance(accountId, authorizationHeader);
        return ResponseEntity.ok(response);
    }
}