package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.Transaction;

import java.util.List;

public interface LoadTransactionPort {

    List<Transaction> findByAccountNumber(String accountNumber);
}
