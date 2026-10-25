package com.eddiapps.banking.application.port.out;

import com.eddiapps.banking.domain.model.Account;

public interface SaveAccountPort {
    Account save(Account account);
}
