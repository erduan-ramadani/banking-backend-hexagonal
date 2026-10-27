package com.eddiapps.banking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.LoadIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.application.port.out.SaveIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveTransactionPort;
import com.eddiapps.banking.domain.exception.InvalidAmountException;
import com.eddiapps.banking.domain.model.Account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class DepositApplicationServiceTest {
    @Mock
    LoadAccountPort loadAccountPort;
    @Mock
    SaveAccountPort saveAccountPort;
    @Mock
    SaveTransactionPort saveTransactionPort;
    @Mock
    LoadIdempotencyPort loadIdempotencyPort;
    @Mock
    SaveIdempotencyPort saveIdempotencyPort;
    @InjectMocks
    DepositApplicationService depositApplicationService;

    static Stream<Arguments> invalidAmounts() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-50)),
                Arguments.of(BigDecimal.ZERO),
                Arguments.of((Object) null)
        );
    }

    @Test
    void shouldDepositMoneySuccessfully() {
        String idempotencyKey = "deposit-key-123";
        Account account = new Account(1L, "1", BigDecimal.valueOf(100), 1L);

        when(loadAccountPort.findByAccountNumber("1")).thenReturn(Optional.of(account));
        when(saveAccountPort.save(account)).thenReturn(account);
        when(loadIdempotencyPort.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());

        Account updatedAccount = depositApplicationService
                .deposit(idempotencyKey, "1", BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), updatedAccount.getBalance());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(saveAccountPort).save(accountCaptor.capture());
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(BigDecimal.valueOf(150), capturedAccount.getBalance());

        verify(saveTransactionPort).saveDeposit("1", BigDecimal.valueOf(50));
        verify(saveIdempotencyPort).save(idempotencyKey, "DEPOSIT | 1 | 50", account);
    }

    @Test
    void shouldCreateAccountWhenAccountNotFound() {
        String idempotencyKey = "deposit-key-123";

        when(loadAccountPort.findByAccountNumber("99")).thenReturn(Optional.empty());
        when(saveAccountPort.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account newAccount = depositApplicationService.deposit(idempotencyKey, "99", BigDecimal.valueOf(50));

        assertNotNull(newAccount);
        assertEquals("99", newAccount.getAccountNumber());
        assertEquals(BigDecimal.valueOf(50), newAccount.getBalance());

        verify(saveAccountPort).save(newAccount);
        verify(saveTransactionPort).saveDeposit("99", BigDecimal.valueOf(50));
    }

    @ParameterizedTest
    @MethodSource("invalidAmounts")
    void shouldRejectInvalidDepositAmounts(BigDecimal amount) {
        String idempotencyKey = "deposit-key-123";

        assertThrows(InvalidAmountException.class,
                () -> depositApplicationService.deposit(idempotencyKey, "1", amount)
        );

        verifyNoInteractions(loadAccountPort, saveAccountPort);
    }

    @Test
    void shouldPropagateExceptionWhenSavingFails() {
        String idempotencyKey = "deposit-key-123";
        Account account = new Account(1L, "1", BigDecimal.valueOf(100), 1L);

        when(loadIdempotencyPort.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(loadAccountPort.findByAccountNumber("1")).thenReturn(Optional.of(account));
        when(saveAccountPort.save(account)).thenThrow(new OptimisticLockingFailureException("Version conflict"));


        assertThrows(OptimisticLockingFailureException.class,
                () -> depositApplicationService.deposit(idempotencyKey, "1", BigDecimal.valueOf(50))
        );
    }

}
