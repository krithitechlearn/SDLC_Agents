package com.example.balance.service;

import com.example.balance.dto.AccountBalanceResponse;
import org.springframework.stereotype.Service;

@Service
public class AccountBalanceService {

    public AccountBalanceResponse getAccountBalance(String accountId) {
        // Mock implementation for demonstration
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setAccountId(accountId);
        response.setCurrentBalance(1000.50);
        response.setCurrency("USD");
        response.setLastUpdated(java.time.LocalDateTime.now());
        return response;
    }
}