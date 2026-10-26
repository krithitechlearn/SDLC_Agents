package com.example.balance.controller;

import com.example.balance.dto.AccountBalanceResponse;
import com.example.balance.service.AccountBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountBalanceController {

    @Autowired
    private AccountBalanceService accountBalanceService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable String accountId) {
        AccountBalanceResponse response = accountBalanceService.getAccountBalance(accountId);
        return ResponseEntity.ok(response);
    }
}