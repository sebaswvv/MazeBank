package w.mazebank.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.InsufficientFundsException;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.services.TransactionServiceJpa;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id, @AuthenticationPrincipal User userPerforming)
        throws TransactionNotFoundException {
        return ResponseEntity.ok(transactionServiceJpa.getTransactionAndValidate(id, userPerforming));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest, @AuthenticationPrincipal User userPerforming)
        throws TransactionFailedException, InsufficientFundsException, AccountNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionServiceJpa.postTransaction(transactionRequest, userPerforming));
    }
}