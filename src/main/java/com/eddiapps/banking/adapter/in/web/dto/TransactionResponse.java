package com.eddiapps.banking.adapter.in.web.dto;

import com.eddiapps.banking.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        TransactionType type,
        BigDecimal amount,
        Instant timestamp,
        String senderAccountNumber,
        String recipientAccountNumber
) {
}
