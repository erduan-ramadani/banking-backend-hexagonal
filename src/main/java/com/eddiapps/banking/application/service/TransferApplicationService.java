package com.eddiapps.banking.application.service;

import com.eddiapps.banking.application.port.in.TransferUseCase;
import com.eddiapps.banking.application.port.out.LoadAccountPort;
import com.eddiapps.banking.application.port.out.LoadIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveAccountPort;
import com.eddiapps.banking.application.port.out.SaveIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveTransactionPort;
import com.eddiapps.banking.domain.exception.InvalidAmountException;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.domain.model.IdempotencyRecord;
import com.eddiapps.banking.exception.AccountNotFoundException;
import com.eddiapps.banking.exception.IdempotencyConflictException;
import com.eddiapps.banking.exception.InvalidIdempotencyKeyException;
import com.eddiapps.banking.exception.InvalidTransferException;
import com.eddiapps.banking.model.TransactionType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class TransferApplicationService implements TransferUseCase {

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;
    private final LoadIdempotencyPort loadIdempotencyPort;
    private final SaveIdempotencyPort saveIdempotencyPort;

    public TransferApplicationService(
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
    public Account transfer(String idempotencyKey, String senderAccountNumber, String recipientAccountNumber, BigDecimal amount) {
        validateAccountNumbers(senderAccountNumber, recipientAccountNumber);
        validateAmount(amount);
        validateIdempotencyKey(idempotencyKey);
        validateSameAccountTransfer(senderAccountNumber, recipientAccountNumber);

        String requestFingerprint = buildTransferFingerprint(senderAccountNumber, recipientAccountNumber, amount);

        Optional<IdempotencyRecord> idempotencyRecord = loadIdempotencyPort.findByIdempotencyKey(idempotencyKey);
        if (idempotencyRecord.isPresent()) {
            IdempotencyRecord existingRecord = idempotencyRecord.get();
            String savedFingerprint = existingRecord.requestFingerprint();
            if (!requestFingerprint.equals(savedFingerprint)) {
                throw new IdempotencyConflictException("Same idempotency key used with a different request");
            }

            return new Account(null, existingRecord.accountNumber(), existingRecord.balance(), null);
        }

        Account sender = loadAccountPort.findByAccountNumber(senderAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Sender not found"));
        Account recipient = loadAccountPort.findByAccountNumber(recipientAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Recipient not found"));

        sender.withdraw(amount);
        recipient.deposit(amount);

        Account savedSenderAccount = saveAccountPort.save(sender);
        saveAccountPort.save(recipient);

        saveTransactionPort.saveTransfer(senderAccountNumber, amount, TransactionType.TRANSFER_OUTGOING);
        saveTransactionPort.saveTransfer(recipientAccountNumber, amount, TransactionType.TRANSFER_INCOMING);

        saveIdempotencyPort.save(idempotencyKey, requestFingerprint, savedSenderAccount);

        return savedSenderAccount;
    }

    private void validateAccountNumbers(String senderAccountNumber, String recipientAccountNumber) {
        if (senderAccountNumber == null || senderAccountNumber.isBlank()) {
            throw new InvalidTransferException("Sender account number not valid");
        }

        if (recipientAccountNumber == null || recipientAccountNumber.isBlank()) {
            throw new InvalidTransferException("Recipient account number not valid");
        }
    }

    private void validateSameAccountTransfer(String senderAccountNumber, String recipientAccountNumber) {
        if (senderAccountNumber.equals(recipientAccountNumber)) {
            throw new InvalidTransferException("Transfer to same account is not allowed");
        }
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


    private String buildTransferFingerprint(String senderAccountNumber, String recipientAccountNumber, BigDecimal amount) {
        String normalizedAmount = amount.stripTrailingZeros().toPlainString();
        return TransactionType.TRANSFER.name() + " | "
                + senderAccountNumber + " | "
                + recipientAccountNumber + " | "
                + normalizedAmount;
    }
}
