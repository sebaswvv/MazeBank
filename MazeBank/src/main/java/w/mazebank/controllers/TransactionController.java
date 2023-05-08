package w.mazebank.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.LoginRequest;
import w.mazebank.models.requests.RefreshRequest;
import w.mazebank.models.requests.RegisterRequest;
import w.mazebank.models.responses.AuthenticationResponse;
import w.mazebank.models.responses.RefreshResponse;
import w.mazebank.services.AuthService;
import w.mazebank.services.TransactionServiceJpa;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getUserById(@PathVariable Long id, @AuthenticationPrincipal User user)
        throws TransactionNotFoundException {
        Transaction transaction = transactionServiceJpa.getTransactionAndValidate(id, user);
        return ResponseEntity.ok(transaction);
    }


}