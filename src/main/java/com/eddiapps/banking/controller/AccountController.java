package com.eddiapps.banking.controller;

import com.eddiapps.banking.application.port.in.DepositUseCase;
import com.eddiapps.banking.domain.model.Account;
import com.eddiapps.banking.dto.AccountResponse;
import com.eddiapps.banking.dto.AmountRequest;
import com.eddiapps.banking.dto.TransactionResponse;
import com.eddiapps.banking.dto.TransferRequest;
import com.eddiapps.banking.service.AccountService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final DepositUseCase depositUseCase;

    public AccountController(AccountService accountService, DepositUseCase depositUseCase) {
        this.accountService = accountService;
        this.depositUseCase = depositUseCase;
    }

    @PostMapping("/{accountNumber}/deposit")
    AccountResponse deposit(
            @PathVariable String accountNumber,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody AmountRequest request
    ) {
        Account account = depositUseCase.deposit(idempotencyKey, accountNumber, request.amount());
        return new AccountResponse(account.getAccountNumber(), account.getBalance());
    }

    @PostMapping("/{accountNumber}/withdraw")
    AccountResponse withdraw(
            @PathVariable String accountNumber,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody AmountRequest request
    ) {
        return accountService.withdraw(idempotencyKey, accountNumber, request.amount());
    }

    @GetMapping("/{accountNumber}")
    AccountResponse getBalance(
            @PathVariable String accountNumber
    ) {
        return accountService.getBalance(accountNumber);
    }

    @PostMapping("/transfer")
    AccountResponse transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {
        return accountService.transfer(
                idempotencyKey,
                request.senderAccountNumber(),
                request.recipientAccountNumber(),
                request.amount()
        );
    }

    @GetMapping("/{accountNumber}/history")
    List<TransactionResponse> getHistory(@PathVariable String accountNumber) {
        return accountService.getTransactionHistory(accountNumber);
    }
}
