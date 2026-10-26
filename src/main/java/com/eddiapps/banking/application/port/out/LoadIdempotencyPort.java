package com.eddiapps.banking.application.port.out;

import java.util.Optional;

public interface LoadIdempotencyPort {
    Optional<String> findRequestFingerprint(String idempotencyKey);
}
