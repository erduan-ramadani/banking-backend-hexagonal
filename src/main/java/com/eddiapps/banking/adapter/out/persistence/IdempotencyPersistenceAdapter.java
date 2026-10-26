package com.eddiapps.banking.adapter.out.persistence;

import com.eddiapps.banking.application.port.out.LoadIdempotencyPort;
import com.eddiapps.banking.application.port.out.SaveIdempotencyPort;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.entity.IdempotencyRecordEntity;
import com.eddiapps.banking.repository.IdempotencyRecordRepository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class IdempotencyPersistenceAdapter implements LoadIdempotencyPort, SaveIdempotencyPort {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    public IdempotencyPersistenceAdapter(IdempotencyRecordRepository idempotencyRecordRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    @Override
    public Optional<String> findRequestFingerprint(String idempotencyKey) {
        return idempotencyRecordRepository
                .findByIdempotencyKey(idempotencyKey)
                .map(IdempotencyRecordEntity::getRequestFingerprint);
    }

    @Override
    public void save(String idempotencyKey, String requestFingerprint, Account account) {
        IdempotencyRecordEntity idempotencyRecordEntity = new IdempotencyRecordEntity(
                idempotencyKey,
                requestFingerprint,
                account.getAccountNumber(),
                account.getBalance(),
                Instant.now()
        );
        idempotencyRecordRepository.save(idempotencyRecordEntity);
    }
}
