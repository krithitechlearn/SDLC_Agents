package com.example.account.controller;

import com.example.account.dto.AccountBalanceResponse;
import com.example.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @PathVariable String accountId,
            @RequestHeader("Authorization") String authorizationToken) {
        AccountBalanceResponse response = accountService.getAccountBalance(accountId, authorizationToken);
        return ResponseEntity.ok(response);
    }
}
