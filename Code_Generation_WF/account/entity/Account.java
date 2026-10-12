package com.example.account.entity;

     import javax.persistence.Entity;
     import javax.persistence.Id;

     @Entity
     public class Account {
         @Id
         private String accountId;
         private String accountNumber;
         private String currency;

         // Getters and Setters
         public String getAccountId() {
             return accountId;
         }

         public void setAccountId(String accountId) {
             this.accountId = accountId;
         }

         public String getAccountNumber() {
             return accountNumber;
         }

         public void setAccountNumber(String accountNumber) {
             this.accountNumber = accountNumber;
         }

         public String getCurrency() {
             return currency;
         }

         public void setCurrency(String currency) {
             this.currency = currency;
         }
     }
