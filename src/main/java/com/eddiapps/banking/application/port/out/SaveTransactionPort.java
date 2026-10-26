package com.eddiapps.banking.application.port.out;

import java.math.BigDecimal;

public interface SaveTransactionPort {
    void saveDeposit(String accountNumber, BigDecimal amount);

    void saveWithdrawal(String accountNumber, BigDecimal amount);
}
