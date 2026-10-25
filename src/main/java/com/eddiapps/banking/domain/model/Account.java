package com.eddiapps.banking.domain.model;

import com.eddiapps.banking.domain.exception.InsufficientFundsException;
import com.eddiapps.banking.domain.exception.InvalidAmountException;

import java.math.BigDecimal;

public class Account {

    private final Long id;
    private final String accountNumber;
    private final Long version;
    private BigDecimal balance;

    public Account(Long id, String accountNumber, BigDecimal balance, Long version) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.version = version;
    }

    public static Account createNew(String accountNumber) {
        return new Account(null, accountNumber, BigDecimal.ZERO, null);
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

    public Long getVersion() {
        return version;
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);

        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        balance = balance.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be > 0");
        }
    }
}
