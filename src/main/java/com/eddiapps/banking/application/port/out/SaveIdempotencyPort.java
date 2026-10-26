package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.Account;

public interface SaveIdempotencyPort {
    void save(String idempotencyKey, String requestFingerprint, Account account);
}
