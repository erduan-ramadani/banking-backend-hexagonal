package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.DepositUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.domain.model.Account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class DepositApplicationService implements DepositUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;

    public DepositApplicationService(LoadAccountPort loadAccountPort, SaveAccountPort saveAccountPort) {
        this.loadAccountPort = loadAccountPort;
        this.saveAccountPort = saveAccountPort;
    }

    @Override
    public Account deposit(String idempotencyKey, String accountNumber, BigDecimal amount) {
        Account account = loadAccountPort.findByAccountNumber(accountNumber)
                .orElseGet(() -> Account.createNew(accountNumber));
        account.deposit(amount);
        return saveAccountPort.save(account);
    }
}
