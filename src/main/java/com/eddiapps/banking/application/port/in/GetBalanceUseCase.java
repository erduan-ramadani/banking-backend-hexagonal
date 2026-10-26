package com.eddiapps.banking.application.port.in;

import com.eddiapps.banking.domain.model.Account;

public interface GetBalanceUseCase {
    Account getBalance(String accountNumber);
}
