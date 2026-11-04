package com.example.account.service;

import com.example.account.dto.AccountBalanceResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountService {

    private static final double DEFAULT_BALANCE = 1000.00;
    private static final String DEFAULT_CURRENCY = "USD";

    public AccountBalanceResponse getAccountBalance(String accountId, String authorizationHeader) {
        AccountBalanceResponse response = new AccountBalanceResponse();
        response.setAccountId(accountId);
        response.setCurrentBalance(DEFAULT_BALANCE);
        response.setCurrency(DEFAULT_CURRENCY);
        response.setLastUpdated(LocalDateTime.now());
        return response;
    }
}
