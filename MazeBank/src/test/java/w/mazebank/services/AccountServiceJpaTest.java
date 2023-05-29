package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import w.mazebank.enums.AccountType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.AccountStatusException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceJpaTest {

    @InjectMocks
    private AccountServiceJpa accountServiceJpa;

    @Mock
    private AccountRepository accountRepository;

    List<User> users;
    List<Account> accounts;

    @BeforeEach
    void setUp() {
        // create two users
        users = new ArrayList<>();
        users.add(User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build());
        users.add(User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .build()
        );

        // create two accounts
        accounts = new ArrayList<>();
        accounts.add(Account.builder()
                .id(1L)
                .accountType(AccountType.CHECKING)
                .balance(1000.00)
                .user(users.get(0))
                .build());
        accounts.add(Account.builder()
                .id(2L)
                .accountType(AccountType.SAVINGS)
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
        assertEquals(1L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
    }

    @Test
    void NoAccountFoundById() {
        // mock the findById method and return an account
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // call the method
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceJpa.getAccountById(1L);
        });

        // test results
        assertEquals("Account with id: " + 1L + " not found", exception.getMessage());
    }

    @Test
    // Happy flow
    void unlockAccount() throws AccountNotFoundException, AccountStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        Account result = accountServiceJpa.unlockAccount(1L);

        // test results
        assertEquals(1L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertTrue(result.isActive());
    }

    @Test
    // Account already unlocked
    void unlockAccountAlreadyUnlocked() throws AccountNotFoundException, AccountStatusException {
        // mock the findById method and return an account
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(accounts.get(0)));

        // call the method
        Account result = accountServiceJpa.unlockAccount(1L);

        // test results
        assertEquals(1L, result.getUser().getId());
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertTrue(result.isActive());

        // call the method again
        AccountStatusException exception = assertThrows(AccountStatusException.class, () -> {
            accountServiceJpa.unlockAccount(1L);
        });

        // test results
        assertEquals("Account is already unlocked", exception.getMessage());
    }

    @Test
    // Account not found
    void unlockAccountNotFound() {
        // mock the findById method and return an account
        when(accountRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // call the method
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceJpa.unlockAccount(1L);
        });

        // test results
        assertEquals("Account with id: " + 1L + " not found", exception.getMessage());
    }
}