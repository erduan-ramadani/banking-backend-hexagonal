package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.DepositUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.application.port.out.SaveTransactionPort;
import com.eddiapps.banking.domain.model.Account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class DepositApplicationService implements DepositUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;

    public DepositApplicationService(
            LoadAccountPort loadAccountPort,
            SaveAccountPort saveAccountPort,
            SaveTransactionPort saveTransactionPort
    ) {
        this.loadAccountPort = loadAccountPort;
        this.saveAccountPort = saveAccountPort;
        this.saveTransactionPort = saveTransactionPort;
    }

    @Override
    public Account deposit(String idempotencyKey, String accountNumber, BigDecimal amount) {
        Account account = loadAccountPort.findByAccountNumber(accountNumber)
                .orElseGet(() -> Account.createNew(accountNumber));
        account.deposit(amount);
        Account savedAccount = saveAccountPort.save(account);
        saveTransactionPort.saveDeposit(accountNumber, amount);
        return savedAccount;
    }
}
