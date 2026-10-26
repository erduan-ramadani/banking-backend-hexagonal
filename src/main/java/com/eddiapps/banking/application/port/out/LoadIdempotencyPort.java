package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.IdempotencyRecord;

import java.util.Optional;

public interface LoadIdempotencyPort {
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

}
