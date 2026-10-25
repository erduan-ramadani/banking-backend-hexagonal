package com.eddiapps.banking.domain.model;

import java.math.BigDecimal;

public class Account {

    private final Long id;
    private final String accountNumber;
    private BigDecimal balance;

    public Account(Long id, String accountNumber, BigDecimal balance){
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
