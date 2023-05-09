package w.mazebank.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.AccountCreationLimitReachedException;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UnauthorizedAccountAccessException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.AccountPatchRequest;
import w.mazebank.models.requests.AccountRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.repositories.AccountRepository;

import java.util.List;

@Service
public class AccountServiceJpa {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    @Autowired
    private UserServiceJpa userServiceJpa;

    private final ModelMapper mapper = new ModelMapper();

    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    public Account getAccountById(Long id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) throw new AccountNotFoundException("Account with id: " + id + " not found");
        return account;
    }

    public Account getAccountAndValidate(Long id, User user) throws AccountNotFoundException {
        Account account = getAccountById(id);
        validateAccountOwner(user, account);
        return account;
    }

    public AccountResponse createAccount(AccountRequest body) throws UserNotFoundException, AccountCreationLimitReachedException {
        User user = userServiceJpa.getUserById(body.getUserId());
        Account account = Account.builder()
            .accountType(body.getAccountType())
            // TODO: custom generator to generate IBAN
            // .iban()
            .isActive(body.isActive())
            .user(user)
            .absoluteLimit(body.getAbsoluteLimit())
            .balance(0.0)
            .build();

        List<Account> accounts = user.getAccounts();
        int checkingAccounts = (int) accounts.stream().filter(a -> a.getAccountType() == AccountType.CHECKING).count();
        int savingsAccounts = (int) accounts.stream().filter(a -> a.getAccountType() == AccountType.SAVINGS).count();

        // check if account creation limit has been reached
        if (account.getAccountType() == AccountType.SAVINGS && savingsAccounts >= 1)
            throw new AccountCreationLimitReachedException("Savings account creation limit reached");
        if (account.getAccountType() == AccountType.CHECKING && checkingAccounts >= 1)
            throw new AccountCreationLimitReachedException("Checking account creation limit reached");

        // save account to database
        Account newAccount = accountRepository.save(account);

        // map account to account response
        TypeMap<Account, AccountResponse> propertyMapper = mapper.typeMap(Account.class, AccountResponse.class);
        // cast accountType to integer with AccountResponse::setAccountType
        propertyMapper.addMapping(Account::getAccountType, AccountResponse::setAccountType);
        return mapper.map(newAccount, AccountResponse.class);
    }

    public AccountResponse updateAccount(long id, AccountPatchRequest body) throws AccountNotFoundException {
        Account account = getAccountById(id);
        if (body.getAbsoluteLimit() != null) {
            account.setAbsoluteLimit(body.getAbsoluteLimit());
        }

        Account updatedAccount = accountRepository.save(account);

        // map account to account response
        TypeMap<Account, AccountResponse> propertyMapper = mapper.typeMap(Account.class, AccountResponse.class);
        // cast accountType to integer with AccountResponse::setAccountType
        propertyMapper.addMapping(Account::getAccountType, AccountResponse::setAccountType);
        return mapper.map(updatedAccount, AccountResponse.class);
    }

    public Transaction deposit(Long accountId, double amount, User userDetails) throws AccountNotFoundException {
        // get account from database and validate owner
        Account account = getAccountAndValidate(accountId, userDetails);

        // use transaction service to deposit money
        return transactionServiceJpa.deposit(account, amount);
    }

    private void validateAccountOwner(User user, Account account) {
        // check if current user is the same as account owner or if current user is an employee
        if (user.getRole() != RoleType.EMPLOYEE && user.getId() != account.getUser().getId()) {
            throw new UnauthorizedAccountAccessException("You are not authorized to access this account");
        }
    }

    public void lockAccount(Long id) throws AccountNotFoundException {
        Account account = getAccountById(id);
        account.setActive(true);

        accountRepository.save(account);
    }

    public void unlockAccount(Long id) throws AccountNotFoundException {
        Account account = getAccountById(id);
        account.setActive(false);

        accountRepository.save(account);
    }
}