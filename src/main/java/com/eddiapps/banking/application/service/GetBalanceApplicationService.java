package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.GetBalanceUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.exception.AccountNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetBalanceApplicationService implements GetBalanceUseCase {

    private final LoadAccountPort loadAccountPort;

    public GetBalanceApplicationService(LoadAccountPort loadAccountPort) {
        this.loadAccountPort = loadAccountPort;
    }

    @Override
    public Account getBalance(String accountNumber) {
        return loadAccountPort.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException("Account does not exist")
        );
    }
}
