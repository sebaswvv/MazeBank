package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import w.mazebank.controllers.AccountController;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UnauthorizedAccountAccessException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.repositories.AccountRepository;

@Service
public class AccountServiceJpa {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    public Account getAccountById(Long id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) throw new AccountNotFoundException("Account with id: " + id + " not found");
        return account;
    }

    public Transaction deposit(Long accountId, double amount, UserDetails userDetails) throws AccountNotFoundException {
        // get account from database and validate owner
        Account account = getAccountById(accountId);
        validateAccountOwner(userDetails, account);

        // use transaction service to deposit money
        Transaction transaction = transactionServiceJpa.deposit(account, amount);
        return transaction;
    }

    private void validateAccountOwner(UserDetails userDetails, Account account) {
        // check if jwt user is the same as account owner
        if (!userDetails.getUsername().equals(account.getUser().getEmail())) {
            throw new UnauthorizedAccountAccessException("You are not authorized to access this account");
        }
    }

    public void lockAccount(Long id) throws AccountNotFoundException {
        // TODO: check if request is done by employee, if not throw NotAuthException??

        Account account = getAccountById(id);
        account.setActive(true);

        accountRepository.save(account);
    }

    public void unlockAccount(Long id) throws AccountNotFoundException {
        // TODO: check if request is done by employee, if not throw NotAuthException??

        Account account = getAccountById(id);
        account.setActive(false);

        accountRepository.save(account);
    }
}