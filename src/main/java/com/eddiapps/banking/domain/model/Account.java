package com.eddiapps.banking.domain.model;

import com.eddiapps.banking.exception.InvalidAmountException;

import java.math.BigDecimal;

public class Account {

    private final Long id;
    private final String accountNumber;
    private BigDecimal balance;

    public Account(Long id, String accountNumber, BigDecimal balance) {
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

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be > 0");
        }

        balance = balance.add(amount);
    }
}
