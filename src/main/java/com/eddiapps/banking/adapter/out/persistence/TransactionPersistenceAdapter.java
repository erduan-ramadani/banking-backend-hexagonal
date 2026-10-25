package com.eddiapps.banking.adapter.out.persistence;

import com.eddiapps.banking.application.port.out.SaveTransactionPort;
import com.eddiapps.banking.entity.TransactionEntity;
import com.eddiapps.banking.model.TransactionType;
import com.eddiapps.banking.repository.TransactionRepository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class TransactionPersistenceAdapter implements SaveTransactionPort {
    private final TransactionRepository transactionRepository;

    public TransactionPersistenceAdapter(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void saveDeposit(String accountNumber, BigDecimal amount) {
        transactionRepository.save(new TransactionEntity(accountNumber, TransactionType.DEPOSIT, amount));
    }
}
