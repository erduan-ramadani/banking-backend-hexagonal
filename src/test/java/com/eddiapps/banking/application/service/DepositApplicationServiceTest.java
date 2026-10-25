package com.eddiapps.banking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.exception.AccountNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DepositApplicationServiceTest {
    @Mock
    LoadAccountPort loadAccountPort;
    @Mock
    SaveAccountPort saveAccountPort;
    @InjectMocks
    DepositApplicationService depositApplicationService;

    @Test
    void shouldDepositMoneySuccessfully() {
        Account account = new Account(1L, "1", BigDecimal.valueOf(100), 1L);

        when(loadAccountPort.findById(1L)).thenReturn(Optional.of(account));
        when(saveAccountPort.save(account)).thenReturn(account);

        Account updatedAccount = depositApplicationService.deposit(1L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), updatedAccount.getBalance());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(saveAccountPort).save(accountCaptor.capture());
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(BigDecimal.valueOf(150), capturedAccount.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        when(loadAccountPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> depositApplicationService.deposit(99L, BigDecimal.valueOf(50)));

        verify(saveAccountPort, never()).save(any(Account.class));
    }

}
