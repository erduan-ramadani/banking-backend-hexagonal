package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.TransactionType;

import java.math.BigDecimal;

public interface SaveTransactionPort {
    void saveDeposit(String accountNumber, BigDecimal amount);

    void saveWithdrawal(String accountNumber, BigDecimal amount);

    void saveTransfer(String accountNumber, String senderAccountNumber, String recipientAccountNumber, BigDecimal amount, TransactionType type);
}
