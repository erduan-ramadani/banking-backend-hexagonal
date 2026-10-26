package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.GetTransactionHistoryUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.LoadTransactionPort;
import com.eddiapps.banking.domain.model.Transaction;
import com.eddiapps.banking.domain.exception.AccountNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetTransactionApplicationService implements GetTransactionHistoryUseCase {

    private final LoadTransactionPort loadTransactionPort;
    private final LoadAccountPort loadAccountPort;

    public GetTransactionApplicationService(LoadTransactionPort loadTransactionPort, LoadAccountPort loadAccountPort) {
        this.loadTransactionPort = loadTransactionPort;
        this.loadAccountPort = loadAccountPort;
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        loadAccountPort.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException("Account not found")
        );
        return loadTransactionPort.findByAccountNumber(accountNumber);
    }
}
