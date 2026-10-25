package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Account;

import java.math.BigDecimal;

public interface DepositUseCase {
    Account deposit(Long accountId, BigDecimal amount);
}
