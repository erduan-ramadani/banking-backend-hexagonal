package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.Account;

import java.util.Optional;

public interface LoadAccountPort {
    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(String accountNumber);
}
