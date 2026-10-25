package com.eddiapps.banking.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.entity.AccountEntity;
import com.eddiapps.banking.repository.AccountRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountPersistenceAdapterTest {
    @Mock
    AccountRepository accountRepository;
    @InjectMocks
    AccountPersistenceAdapter accountPersistenceAdapter;

    @Test
    void shouldLoadAccountById() {
        AccountEntity account = new AccountEntity("12345");
        account.setId(1L);
        account.setBalance(BigDecimal.valueOf(100));
        account.setVersion(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<Account> savedAccount = accountPersistenceAdapter.findById(1L);

        assertTrue(savedAccount.isPresent());
        Account loadedAccount = savedAccount.get();
        assertEquals(1L, loadedAccount.getId());
        assertEquals("12345", loadedAccount.getAccountNumber());
        assertEquals(BigDecimal.valueOf(100), loadedAccount.getBalance());
        assertEquals(1L, loadedAccount.getVersion());
    }
}
