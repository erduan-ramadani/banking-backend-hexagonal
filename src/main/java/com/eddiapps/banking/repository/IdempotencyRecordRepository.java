package com.eddiapps.banking.repository;

import com.eddiapps.banking.entity.IdempotencyRecordEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecordEntity, Long> {
    Optional<IdempotencyRecordEntity> findByIdempotencyKey(String idempotencyKey);

    int countByIdempotencyKey(String idempotencyKey);
}
