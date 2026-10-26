package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Transaction;

import java.util.List;

public interface GetTransactionHistoryUseCase {
    List<Transaction> getTransactionHistory(String accountNumber);
}
