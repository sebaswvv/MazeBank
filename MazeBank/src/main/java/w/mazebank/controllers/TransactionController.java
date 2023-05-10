package w.mazebank.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.DepositWithdrawResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.services.TransactionServiceJpa;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getUserById(@PathVariable Long id, @AuthenticationPrincipal User user)
        throws TransactionNotFoundException {
        TransactionResponse transactionResponse = transactionServiceJpa.getTransactionAndValidate(id, user);
        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping
    public ResponseEntity<DepositWithdrawResponse> createTransaction(@RequestBody @Valid TransactionRequest transactionRequest, @AuthenticationPrincipal User user) throws TransactionFailedException {
        DepositWithdrawResponse response = transactionServiceJpa.createTransaction(transactionRequest, user);
        return ResponseEntity.ok(response);
    }
}