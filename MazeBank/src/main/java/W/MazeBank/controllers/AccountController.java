package w.mazebank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.UnauthorizedAccountAccessException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.requests.DepositRequest;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.services.AccountServiceJpa;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountServiceJpa accountServiceJpa;


    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
        @PathVariable("accountId") Long accountId,
        @RequestBody DepositRequest depositRequest,
        @AuthenticationPrincipal UserDetails userDetails) {

        // create deposit transaction
        Transaction transaction = accountServiceJpa.deposit(accountId, depositRequest.getAmount(), userDetails);

        // create transaction response and return it
        TransactionResponse transactionResponse = TransactionResponse.builder()
            .message("Deposit successful")
            .build();
        return ResponseEntity.ok(transactionResponse);
    }
}
