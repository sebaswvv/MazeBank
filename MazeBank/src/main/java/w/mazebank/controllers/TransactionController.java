package w.mazebank.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        throws TransactionNotFoundException, AccountNotFoundException {
        TransactionResponse transactionResponse = transactionServiceJpa.getTransactionAndValidate(id, userPerforming);
        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest, @AuthenticationPrincipal User user)
        throws TransactionFailedException, InsufficientFundsException, AccountNotFoundException {
        TransactionResponse response = transactionServiceJpa.postTransaction(transactionRequest, user);
        return ResponseEntity.status(201).body(response);
    }
}