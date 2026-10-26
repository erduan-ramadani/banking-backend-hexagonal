package com.eddiapps.banking.domain.model;

import com.eddiapps.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Instant timestamp;
    private Long id;
    private String senderAccountNumber;
    private String recipientAccountNumber;

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount, Instant timestamp) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount, Instant timestamp, String senderAccountNumber, String recipientAccountNumber) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.senderAccountNumber = senderAccountNumber;
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }
}

