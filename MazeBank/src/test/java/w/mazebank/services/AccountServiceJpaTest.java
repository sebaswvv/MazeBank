package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import w.mazebank.enums.AccountType;
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
}