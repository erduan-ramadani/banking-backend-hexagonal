package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Account;

import java.math.BigDecimal;

public interface DepositUseCase {
    Account deposit(String idempotencyKey, String accountNumber, BigDecimal amount);
}
