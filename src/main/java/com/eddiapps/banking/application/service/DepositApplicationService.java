package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.DepositUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.LoadIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.application.port.out.SaveIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveTransactionPort;
import com.eddiapps.banking.domain.exception.InvalidAmountException;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.domain.model.IdempotencyRecord;
import com.eddiapps.banking.exception.IdempotencyConflictException;
import com.eddiapps.banking.exception.InvalidIdempotencyKeyException;
import com.eddiapps.banking.model.TransactionType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class DepositApplicationService implements DepositUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;
    private final LoadIdempotencyPort loadIdempotencyPort;
    private final SaveIdempotencyPort saveIdempotencyPort;

    public DepositApplicationService(
            LoadAccountPort loadAccountPort,
            SaveAccountPort saveAccountPort,
            SaveTransactionPort saveTransactionPort,
            LoadIdempotencyPort loadIdempotencyPort,
            SaveIdempotencyPort saveIdempotencyPort
    ) {
        this.loadAccountPort = loadAccountPort;
        this.saveAccountPort = saveAccountPort;
        this.saveTransactionPort = saveTransactionPort;
        this.loadIdempotencyPort = loadIdempotencyPort;
        this.saveIdempotencyPort = saveIdempotencyPort;
    }

    @Override
    public Account deposit(String idempotencyKey, String accountNumber, BigDecimal amount) {
        validateAmount(amount);
        validateIdempotencyKey(idempotencyKey);

        String requestFingerprint = buildFingerprint(TransactionType.DEPOSIT, accountNumber, amount);

        Optional<IdempotencyRecord> idempotencyRecord = loadIdempotencyPort.findByIdempotencyKey(idempotencyKey);
        if (idempotencyRecord.isPresent()) {
            IdempotencyRecord existingRecord = idempotencyRecord.get();
            String savedFingerprint = existingRecord.requestFingerprint();
            if (!requestFingerprint.equals(savedFingerprint)) {
                throw new IdempotencyConflictException("Same idempotency key used with a different request");
            }

            return new Account(null, existingRecord.accountNumber(), existingRecord.balance(), null);
        }

        Account account = loadAccountPort.findByAccountNumber(accountNumber)
                .orElseGet(() -> Account.createNew(accountNumber));
        account.deposit(amount);
        Account savedAccount = saveAccountPort.save(account);

        saveTransactionPort.saveDeposit(accountNumber, amount);
        saveIdempotencyPort.save(idempotencyKey, requestFingerprint, savedAccount);

        return savedAccount;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidIdempotencyKeyException("The idempotency key is not valid");
        }
    }

    private String buildFingerprint(TransactionType type, String accountNumber, BigDecimal amount) {
        String normalizedAmount = amount.stripTrailingZeros().toPlainString();
        return type.name() + " | "
                + accountNumber + " | "
                + normalizedAmount;
    }
}
