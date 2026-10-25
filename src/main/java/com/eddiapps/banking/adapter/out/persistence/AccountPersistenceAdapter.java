package com.eddiapps.banking.adapter.out.persistence;

import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.entity.AccountEntity;
import com.eddiapps.banking.exception.AccountNotFoundException;
import com.eddiapps.banking.repository.AccountRepository;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class AccountPersistenceAdapter implements LoadAccountPort, SaveAccountPort {
    private final AccountRepository accountRepository;

    public AccountPersistenceAdapter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).map(this::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity;
        if (account.getId() == null) {
            accountEntity = new AccountEntity(account.getAccountNumber());
        } else {
            accountEntity = accountRepository.findById(account.getId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            if (!Objects.equals(account.getVersion(), accountEntity.getVersion())) {
                throw new OptimisticLockingFailureException("Version conflict");
            }
        }


        accountEntity.setBalance(account.getBalance());
        AccountEntity savedAccount = accountRepository.save(accountEntity);
        return toDomain(savedAccount);
    }

    private Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getVersion()
        );
    }
}
