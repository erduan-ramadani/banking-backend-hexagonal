package com.eddiapps.banking.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record IdempotencyRecord(String idempotencyKey, String requestFingerprint, String accountNumber,
                                BigDecimal balance, Instant createdAt) {
}
