package com.example.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.account.dto.AccountBalanceResponse;
import com.example.account.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(
            @PathVariable String accountId,
            @RequestHeader("Authorization") String authorizationHeader) {
        AccountBalanceResponse response = accountService.getAccountBalance(accountId, authorizationHeader);
        return ResponseEntity.ok(response);
    }
}