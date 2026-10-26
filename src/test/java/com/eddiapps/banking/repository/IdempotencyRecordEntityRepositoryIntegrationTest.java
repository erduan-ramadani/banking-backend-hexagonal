package com.eddiapps.banking.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eddiapps.banking.entity.IdempotencyRecordEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@DataJpaTest
public class IdempotencyRecordEntityRepositoryIntegrationTest {

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Test
    void saveDuplicateIdempotencyKey_throwsDataIntegrityViolationException() {
        String idempotencyKey = "duplicate-key123";

        String requestFingerprint1 = "DEPOSIT | 1 | 40";
        IdempotencyRecordEntity idempotencyRecordEntity1 = new IdempotencyRecordEntity(
                idempotencyKey,
                requestFingerprint1,
                "1",
                BigDecimal.valueOf(100),
                Instant.now()
        );
        idempotencyRecordRepository.saveAndFlush(idempotencyRecordEntity1);

        String requestFingerprint2 = "WITHDRAWAL | 2 | 50";
        IdempotencyRecordEntity idempotencyRecordEntity2 = new IdempotencyRecordEntity(
                idempotencyKey,
                requestFingerprint2,
                "2",
                BigDecimal.valueOf(100),
                Instant.now()
        );
        assertThrows(DataIntegrityViolationException.class,
                () -> idempotencyRecordRepository.saveAndFlush(idempotencyRecordEntity2)
        );
    }

    @Test
    void findByIdempotencyKey_withExistingRecord_returnsRecord() {
        String idempotencyKey = "deposit-key123";
        String requestFingerprint = "DEPOSIT | 1 | 100";
        IdempotencyRecordEntity idempotencyRecordEntity = new IdempotencyRecordEntity(
                idempotencyKey,
                requestFingerprint,
                "1",
                BigDecimal.valueOf(50),
                Instant.now()
        );
        idempotencyRecordRepository.saveAndFlush(idempotencyRecordEntity);
        Optional<IdempotencyRecordEntity> loadedIdempotencyRecord = idempotencyRecordRepository.findByIdempotencyKey(idempotencyKey);

        assertTrue(loadedIdempotencyRecord.isPresent());
        assertEquals(idempotencyRecordEntity.getIdempotencyKey(), loadedIdempotencyRecord.get().getIdempotencyKey());
        assertEquals(idempotencyRecordEntity.getRequestFingerprint(), loadedIdempotencyRecord.get().getRequestFingerprint());
    }
}
