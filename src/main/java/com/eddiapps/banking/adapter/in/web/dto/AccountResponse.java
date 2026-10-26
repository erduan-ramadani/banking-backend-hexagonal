package com.eddiapps.banking.adapter.in.web.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        BigDecimal balance
) {

}
