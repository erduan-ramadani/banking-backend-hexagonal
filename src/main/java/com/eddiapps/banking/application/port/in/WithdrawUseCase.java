package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Account;

import java.math.BigDecimal;

public interface WithdrawUseCase {
    Account withdraw(String idempotencyKey, String accountNumber, BigDecimal amount);
}
