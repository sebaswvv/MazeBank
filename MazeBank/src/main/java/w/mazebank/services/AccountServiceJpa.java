package w.mazebank.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.AccountPatchRequest;
import w.mazebank.models.requests.AccountRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.IbanResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;
import w.mazebank.utils.IbanGenerator;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceJpa extends BaseServiceJpa {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionServiceJpa transactionServiceJpa;

    @Autowired
    private UserServiceJpa userServiceJpa;

    private final ModelMapper mapper = new ModelMapper();

    public AccountResponse createAccount(AccountRequest body) throws UserNotFoundException, AccountCreationLimitReachedException {
        // Get user and account type from request body
        User user = userServiceJpa.getUserById(body.getUserId());
        AccountType accountType = body.getAccountType();
        List<Account> accounts = user.getAccounts();

        // Check account creation limits
        validateAccountCreationLimits(accountType, accounts);

        // Create account and save it to the database
        Account account = buildAccount(accountType, user, body.isActive(), body.getAbsoluteLimit());
        Account newAccount = accountRepository.save(account);

        return createAccountResponse(newAccount);
    }

    private void validateAccountCreationLimits(AccountType accountType, List<Account> accounts) throws AccountCreationLimitReachedException {
        // Count the number of checking and savings accounts
        long checkingAccounts = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.CHECKING)
            .count();

        // Count the number of savings accounts
        long savingsAccounts = accounts.stream()
            .filter(a -> a.getAccountType() == AccountType.SAVINGS)
            .count();

        // Check account creation limits
        if (accountType == AccountType.SAVINGS) {
            if (checkingAccounts == 0) {
                throw new AccountCreationLimitReachedException("You need a checking account to create a savings account");
            }
            if (savingsAccounts >= 1) {
                throw new AccountCreationLimitReachedException("Savings account creation limit reached");
            }
        } else if (accountType == AccountType.CHECKING && checkingAccounts >= 1) {
            throw new AccountCreationLimitReachedException("Checking account creation limit reached");
        }
    }

    private Account buildAccount(AccountType accountType, User user, boolean isActive, Double absoluteLimit) {
        return Account.builder()
            .accountType(accountType)
            .iban(IbanGenerator.generate())
            .isActive(isActive)
            .user(user)
            .absoluteLimit(absoluteLimit)
            .balance(0.0)
            .build();
    }

    public List<AccountResponse> getAllAccounts(int pageNumber, int pageSize, String sort, String search) {
        List<Account> accounts = findAllPaginationAndSort(pageNumber, pageSize, sort, search, accountRepository);

        return mapAccountsToAccountResponses(accounts);
    }

    private List<AccountResponse> mapAccountsToAccountResponses(List<Account> accounts) {
        List<AccountResponse> accountResponses = new ArrayList<>(accounts.size());
        for (Account account : accounts) {
            AccountResponse accountResponse = createAccountResponse(account);
            accountResponses.add(accountResponse);
        }

        return accountResponses;
    }

    public Account getAccountById(Long id) throws AccountNotFoundException {
        return accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException("Account with id: " + id + " not found"));
    }

    public Account getAccountByIban(String iban) throws AccountNotFoundException {
        return accountRepository.findByIban(iban)
            .orElseThrow(() -> new AccountNotFoundException("Account with iban: " + iban + " not found"));
    }

    public List<IbanResponse> getAccountsByName(String name) {
        String[] names = name.split(" ");
        if (names.length == 2) {
            return getAccountsByFirstNameAndLastName(names[0], names[1]);
        }
        return getAccountsByOneName(name);
    }

    private List<IbanResponse> getAccountsByOneName(String name) {
        List<Account> accounts = accountRepository.findAccountsByOneName(name);
        return createListOfIbanResponses(accounts);
    }

    private List<IbanResponse> getAccountsByFirstNameAndLastName(String firstName, String lastName) {
        List<Account> accounts = accountRepository.findAccountsByFirstNameAndLastName(firstName, lastName);
        return createListOfIbanResponses(accounts);
    }

    public AccountResponse updateAccount(long id, AccountPatchRequest body) throws AccountNotFoundException {
        Account account = getAccountById(id);

        // Check if the account is a bank account
        if (account.getIban().equals("NL01INHO0000000001")) {
            throw new UnauthorizedAccountAccessException("Unauthorized access to bank account");
        }

        if (body.getAbsoluteLimit() != null) {
            account.setAbsoluteLimit(body.getAbsoluteLimit());
        }

        Account updatedAccount = accountRepository.save(account);

        // Map account to account response
        return mapper.map(updatedAccount, AccountResponse.class);
    }

    public Account lockAccount(Long id) throws AccountNotFoundException, AccountLockOrUnlockStatusException, UnauthorizedAccountAccessException {
        checkIfAccountIsBank(id);

        Account account = getAccountById(id);
        if (!account.isActive()) {
            throw new AccountLockOrUnlockStatusException("Account is already locked");
        }

        account.setActive(false);
        accountRepository.save(account);
        return account;
    }

    public Account unlockAccount(Long id) throws AccountNotFoundException, AccountLockOrUnlockStatusException, UnauthorizedAccountAccessException {
        checkIfAccountIsBank(id);

        Account account = getAccountById(id);
        if (account.isActive()) {
            throw new AccountLockOrUnlockStatusException("Account is already unlocked");
        }

        account.setActive(true);
        accountRepository.save(account);
        return account;
    }

    private void checkIfAccountIsBank(Long id) {
        if (id == 1) {
            throw new UnauthorizedAccountAccessException("Unauthorized access to bank account");
        }
    }

    public TransactionResponse deposit(Long accountId, double amount, User userDetails) throws AccountNotFoundException, InvalidAccountTypeException, TransactionFailedException {
        Account account = getAccountAndValidate(accountId, userDetails);
        return performAtmTransaction(account, amount, TransactionType.DEPOSIT, userDetails);
    }

    public TransactionResponse withdraw(Long accountId, double amount, User userDetails) throws AccountNotFoundException, InvalidAccountTypeException, TransactionFailedException {
        Account account = getAccountAndValidate(accountId, userDetails);
        validateCheckingAccount(account);
        return performAtmTransaction(account, amount, TransactionType.WITHDRAWAL, userDetails);
    }

    public List<TransactionResponse> getTransactionsFromAccount(int pageNumber, int pageSize, String sort, User user, Long accountId) throws AccountNotFoundException {
        // check if the user has access to the account - employee or the right customer
        validateAccountAccess(accountId);

        // get the account and validate if it exists
        Sort sortObject = createSortObject(sort);
        Pageable pageable = createPageable(pageNumber, pageSize, sortObject);

        // get the transactions and map them to transaction responses
        List<Transaction> transactions = transactionServiceJpa.getTransactionsByUser(accountId, pageable);
        return mapToTransactionResponses(transactions);
    }

    private List<TransactionResponse> mapToTransactionResponses(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionResponse response = createTransactionResponse(transaction);
            transactionResponses.add(response);
        }
        return transactionResponses;
    }

    private TransactionResponse createTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
            .id(transaction.getId())
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .sender(transaction.getSender() != null ? transaction.getSender().getIban() : null)
            .receiver(transaction.getReceiver() != null ? transaction.getReceiver().getIban() : null)
            .transactionType(transaction.getTransactionType().name())
            .timestamp(transaction.getTimestamp().toString())
            .build();
    }

    private AccountResponse createAccountResponse(Account account) {
        return AccountResponse.builder()
            .id(account.getId())
            .accountType(account.getAccountType().getValue())
            .iban(account.getIban())
            .user(createUserResponse(account.getUser()))
            .balance(account.getBalance())
            .absoluteLimit(account.getAbsoluteLimit())
            .active(account.isActive())
            .timestamp(account.getCreatedAt().toString())
            .build();
    }

    private UserResponse createUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .build();
    }

    private  List<IbanResponse> createListOfIbanResponses(List<Account> accounts) {
        List<IbanResponse> ibanResponses = new ArrayList<>();
        for (Account account : accounts) {
            // Skip bank account
            if (account.getIban().equals("NL01INHO0000000001")) {
                continue;
            }

            IbanResponse ibanResponse = createIbanResponse(account);
            ibanResponses.add(ibanResponse);
        }
        return ibanResponses;
    }

    public Account getAccountAndValidate(Long accountId, User user) throws AccountNotFoundException {
        Account account = getAccountById(accountId);
        validateAccountOwner(user, account);
        return account;
    }

    private void validateAccountOwner(User user, Account account) {
        // check if current user is the same as account owner or if current user is an employee
        if (user.getRole() != RoleType.EMPLOYEE && user.getId() != account.getUser().getId()) {
            throw new UnauthorizedAccountAccessException("You are not authorized to access this account");
        }
    }

    private void validateAccountAccess(Long accountId) throws UnauthorizedAccountAccessException {
        checkIfAccountIsBank(accountId);
    }

    private Sort createSortObject(String sort) {
        return Sort.by(Sort.Direction.fromString(sort), "timestamp");
    }

    private Pageable createPageable(int pageNumber, int pageSize, Sort sortObject) {
        return PageRequest.of(pageNumber, pageSize, sortObject);
    }

    private TransactionResponse performAtmTransaction(Account account, double amount, TransactionType transactionType, User userDetails) throws TransactionFailedException, AccountNotFoundException {
        return transactionServiceJpa.atmAction(account, amount, transactionType, userDetails);
    }

    private void validateCheckingAccount(Account account) throws InvalidAccountTypeException {
        if (account.getAccountType() != AccountType.CHECKING) {
            throw new InvalidAccountTypeException("Only checking accounts are allowed for withdrawals");
        }
    }

    private IbanResponse createIbanResponse(Account account){
        return IbanResponse.builder()
            .iban(account.getIban())
            .firstName(account.getUser().getFirstName())
            .lastName(account.getUser().getLastName())
            .build();
    }

    // for the dataseeder
    public void addAccount(Account account) {
        accountRepository.save(account);
    }


}