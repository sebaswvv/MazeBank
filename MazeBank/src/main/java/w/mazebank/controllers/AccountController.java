package w.mazebank.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.requests.AccountPatchRequest;
import w.mazebank.models.requests.AccountRequest;
import w.mazebank.models.requests.AtmRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.IbanResponse;
import w.mazebank.models.responses.LockedResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.services.AccountServiceJpa;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountServiceJpa accountServiceJpa;

    private final ModelMapper mapper = new ModelMapper();

    @GetMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<Object> getAllAccounts(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "asc") String sort,
        @RequestParam(required = false) String search
    ) {
        List<AccountResponse> accounts = accountServiceJpa.getAllAccounts(offset, limit, sort, search);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/iban/{name}")
    public ResponseEntity<Object> getAccountsByName(
        @PathVariable String name
    ) {
        List<IbanResponse> accounts = accountServiceJpa.getAccountsByName(name);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<AccountResponse> getAccountById(
        @PathVariable Long accountId,
        @AuthenticationPrincipal User user
    ) throws AccountNotFoundException {
        Account account = accountServiceJpa.getAccountAndValidate(accountId, user);
        return ResponseEntity.ok(mapper.map(account, AccountResponse.class));
    }

    @PostMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<Object> createAccount(@RequestBody @Valid AccountRequest body) throws UserNotFoundException, AccountCreationLimitReachedException {
        AccountResponse account = accountServiceJpa.createAccount(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PatchMapping("/{accountId}")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<AccountResponse> updateAccount(
        @PathVariable Long accountId,
        @RequestBody @Valid AccountPatchRequest body
    ) throws AccountNotFoundException {
        AccountResponse account = accountServiceJpa.updateAccount(accountId, body);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable("accountId") Long accountId, @RequestBody AtmRequest atmRequest, @AuthenticationPrincipal User user) throws AccountNotFoundException, InvalidAccountTypeException, TransactionFailedException {
        return ResponseEntity.ok(accountServiceJpa.deposit(accountId, atmRequest.getAmount(), user));
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable Long accountId, @RequestBody AtmRequest atmRequest, @AuthenticationPrincipal User user) throws AccountNotFoundException, InvalidAccountTypeException, TransactionFailedException {
        return ResponseEntity.ok(accountServiceJpa.withdraw(accountId, atmRequest.getAmount(), user));
    }

    @PutMapping("/{id}/disable")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<LockedResponse> blockUser(@PathVariable Long id) throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        accountServiceJpa.lockAccount(id);
        return ResponseEntity.ok(new LockedResponse(true));
    }

    @PutMapping("/{id}/enable")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<LockedResponse> unblockUser(@PathVariable Long id) throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        accountServiceJpa.unlockAccount(id);
        return ResponseEntity.ok(new LockedResponse(false));
    }
}
