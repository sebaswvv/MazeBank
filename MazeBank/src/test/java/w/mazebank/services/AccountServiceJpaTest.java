package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import w.mazebank.enums.AccountType;
import w.mazebank.exceptions.AccountCreationLimitReachedException;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.AccountLockOrUnlockStatusException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.requests.AccountRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.IbanResponse;
import w.mazebank.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceJpaTest {

    @InjectMocks
    private AccountServiceJpa accountServiceJpa;

    @Mock
    private UserServiceJpa userServiceJpa;

    @Mock
    private AccountRepository accountRepository;

    List<User> users;
    List<Account> accounts;

    @BeforeEach
    void setUp() {
        // create two users
        users = new ArrayList<>();
        users.add(User.builder()
                .id(2L)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build());
        users.add(User.builder()
                .id(3L)
                .firstName("Jane")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build()
        );

        // create two accounts
        accounts = new ArrayList<>();
        accounts.add(Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .iban("NL01MAZE0000000002")
                .balance(1000.00)
                .user(users.get(0))
                .build());
        accounts.add(Account.builder()
                .id(2L)
                .accountType(AccountType.SAVINGS)
                .iban("NL01MAZE0000000003")
                .balance(2000.00)
                .user(users.get(1))
                .build()
        );
    }

    @Test
    void getAllAccounts() {
        Sort sortObject = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sortObject);
        Page<Account> accountsPage = new PageImpl<>(accounts);

        // mock the findAll method and return users in a page
        when(accountRepository.findAll(pageable)).thenReturn(accountsPage);

        // call the method
        List<AccountResponse> results = accountServiceJpa.getAllAccounts(0, 10, "asc", "");

        // test results
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());

        assertEquals(AccountType.CHECKING.getValue(), results.get(0).getAccountType());
        assertEquals(AccountType.SAVINGS.getValue(), results.get(1).getAccountType());

        assertEquals(1000.00, results.get(0).getBalance());
        assertEquals(2000.00, results.get(1).getBalance());
    }

    @Test
    void getAllAccountsButNoneFound(){
        // clear the accounts list
        accounts.clear();

        Sort sortObject = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sortObject);
        Page<Account> accountsPage = new PageImpl<>(accounts);

        // mock the findAll method and return users in a page
        when(accountRepository.findAll(pageable)).thenReturn(accountsPage);

        // call the method
        List<AccountResponse> results = accountServiceJpa.getAllAccounts(0, 10, "asc", "");

        // test results
        assertEquals(0, results.size());
    }


    @Test
    void getAccountById() throws AccountNotFoundException {
        // mock the findById method and return an account
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        Account result = accountServiceJpa.getAccountById(1L);

        // test results
        assertEquals(2L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
    }

    @Test
    void NoAccountFoundById() {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // call the method
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceJpa.getAccountById(2L);
        });

        // test results
        assertEquals("Account with id: " + 2L + " not found", exception.getMessage());
    }

    @Test
    // Happy flow
    void unlockAccount() throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        Account result = accountServiceJpa.unlockAccount(2L);

        // test results
        assertEquals(2L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertTrue(result.isActive());

       // check if repository was called
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    // Account already unlocked
    void unlockAccountAlreadyUnlocked() throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        Account result = accountServiceJpa.unlockAccount(2L);

        // test results
        assertEquals(2L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertTrue(result.isActive());

        // call the method again
        AccountLockOrUnlockStatusException exception = assertThrows(AccountLockOrUnlockStatusException.class, () -> {
            accountServiceJpa.unlockAccount(2L);
        });

        // test results
        assertEquals("Account is already unlocked", exception.getMessage());
    }

    @Test
    // Account not found
    void unlockAccountNotFound() {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // call the method
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceJpa.unlockAccount(2L);
        });

        // test results
        assertEquals("Account with id: " + 2L + " not found", exception.getMessage());
    }

    @Test
    void createAccount() throws AccountCreationLimitReachedException, UserNotFoundException {
        // Mock userServiceJpa's getUserById method
        when(userServiceJpa.getUserById(2L)).thenReturn(users.get(0));

        // Mock the AccountRepository's save method
        when(accountRepository.save(any(Account.class))).thenReturn(accounts.get(0));

        // Create an AccountRequest object
        AccountRequest accountRequest = AccountRequest.builder()
            .userId(2L)
            .accountType(AccountType.CHECKING)
            .isActive(true)
            .absoluteLimit(5000.00)
            .build();

        // Call the createAccount method
        AccountResponse accountResponse = accountServiceJpa.createAccount(accountRequest);

        // Verify that the UserServiceJpa's getUserById method was called once with the correct argument
        verify(userServiceJpa, times(1)).getUserById(2L);

        // Verify that the AccountRepository's save method was called once with the correct argument
        verify(accountRepository, times(1)).save(any(Account.class));

        // Test the AccountResponse object
        assertNotNull(accountResponse);
        assertEquals(1L, accountResponse.getId());
        assertEquals(AccountType.CHECKING.getValue(), accountResponse.getAccountType());
        assertEquals(1000.00, accountResponse.getBalance());
        assertEquals(2L, accountResponse.getUser().getId());
        assertEquals("John", accountResponse.getUser().getFirstName());
        assertEquals("Doe", accountResponse.getUser().getLastName());
    }

    @Test
    void userAlreadyHasTwoAccount() throws UserNotFoundException {
        // Give the user two accounts
        accounts.add(Account.builder()
            .id(1L)
            .user(users.get(0))
            .accountType(AccountType.CHECKING)
            .isActive(true)
            .absoluteLimit(5000.00)
            .balance(1000.00)
            .build());

        accounts.add(Account.builder()
            .id(2L)
            .user(users.get(0))
            .accountType(AccountType.SAVINGS)
            .isActive(true)
            .absoluteLimit(5000.00)
            .balance(1000.00)
            .build());

        // Set the accounts to user 0
        users.get(0).setAccounts(accounts);

        // Mock userServiceJpa's getUserById method
        when(userServiceJpa.getUserById(1L)).thenReturn(users.get(0));

        // Create an AccountRequest object
        AccountRequest accountRequest = AccountRequest.builder()
            .userId(1L)
            .accountType(AccountType.CHECKING)
            .isActive(true)
            .absoluteLimit(5000.00)
            .build();

        // Call the createAccount method
        AccountCreationLimitReachedException exception = assertThrows(AccountCreationLimitReachedException.class, () -> {
            accountServiceJpa.createAccount(accountRequest);
        });

        // Verify that the UserServiceJpa's getUserById method was called once with the correct argument
        verify(userServiceJpa, times(1)).getUserById(1L);

        // Verify that the AccountRepository's save method was never called
        verify(accountRepository, never()).save(any(Account.class));

        // Test the AccountResponse object
        assertEquals("Checking account creation limit reached", exception.getMessage());
    }

    @Test
    void userDoesNotExists() throws UserNotFoundException {
        // Mock userServiceJpa's getUserById method, to return no user
        when(userServiceJpa.getUserById(1L)).thenThrow(new UserNotFoundException("User with id: " + 1L + " not found"));

        // Create an AccountRequest object
        AccountRequest accountRequest = AccountRequest.builder()
            .userId(1L)
            .accountType(AccountType.CHECKING)
            .isActive(true)
            .absoluteLimit(5000.00)
            .build();

        // Call the createAccount method
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            accountServiceJpa.createAccount(accountRequest);
        });

        // Verify that the UserServiceJpa's getUserById method was called once with the correct argument
        verify(userServiceJpa, times(1)).getUserById(1L);

        // Verify that the AccountRepository's save method was never called
        verify(accountRepository, never()).save(any(Account.class));

        // Test the AccountResponse object
        assertEquals("User with id: " + 1L + " not found", exception.getMessage());
    }

    @Test
    // happy flow
    void lockAccount() throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // unlock the account
        accountServiceJpa.unlockAccount(2L);

        // call the method
        Account result = accountServiceJpa.lockAccount(2L);

        // test results
        assertEquals(2L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertFalse(result.isActive());

        // check if repository was called
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    // Account already locked
    void lockAccountAlreadyLocked() throws AccountNotFoundException, AccountLockOrUnlockStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        AccountLockOrUnlockStatusException exception = assertThrows(AccountLockOrUnlockStatusException.class, () -> {
            accountServiceJpa.lockAccount(2L);
        });

        // test results
        assertEquals("Account is already locked", exception.getMessage());
    }

    @Test
    // Account not found
    void lockAccountNotFound() {
        // mock the findById method and return an account
        when(accountRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // call the method
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceJpa.lockAccount(2L);
        });

        // test results
        assertEquals("Account with id: " + 2L + " not found", exception.getMessage());
    }

    @Test
    // happy flow
    void getAccountsByOneName() {
        // mock the findByName method and return a list of accounts
        when(accountRepository.findAccountsByOneName("John")).thenReturn(accounts);

        // call the method
        List<IbanResponse> result = accountServiceJpa.getAccountsByName("John");

        // test results
        assertEquals("NL01MAZE0000000002", result.get(0).getIban());
        assertEquals("NL01MAZE0000000003", result.get(1).getIban());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(1).getLastName());
    }

    @Test
    void getAccountsByFirstAndLastName() {
        // mock the findByName method and return a list of accounts
        when(accountRepository.findAccountsByFirstNameAndLastName("John", "Doe")).thenReturn(accounts);

        // call the method
        List<IbanResponse> result = accountServiceJpa.getAccountsByName("John Doe");

        // test results
        assertEquals("NL01MAZE0000000002", result.get(0).getIban());
        assertEquals("NL01MAZE0000000003", result.get(1).getIban());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(1).getLastName());
    }


    @Test
    void getAccountsByNameButNoAccountsFound() {
        // mock the findByName method and return an empty list
        when(accountRepository.findAccountsByOneName("John")).thenReturn(new ArrayList<>());

        // call the method
        List<IbanResponse> result = accountServiceJpa.getAccountsByName("John");

        // test results
        assertEquals(0, result.size());
    }
}