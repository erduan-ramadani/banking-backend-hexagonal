package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Account;

import java.math.BigDecimal;

public interface TransferUseCase {
    Account transfer(String idempotencyKey, String senderAccountNumber, String recipientAccountNumber, BigDecimal amount);
}
