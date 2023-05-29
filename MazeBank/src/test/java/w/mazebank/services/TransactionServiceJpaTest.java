package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import w.mazebank.enums.AccountType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceJpaTest {

    @InjectMocks
    private TransactionServiceJpa transactionServiceJpa;

    @Mock
    private AccountServiceJpa accountServiceJpa;

    @Mock
    private UserServiceJpa userServiceJpa;

    @Mock
    private TransactionRepository transactionRepository;

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
            .blocked(false)
            .build());
        users.add(User.builder()
            .id(2L)
            .blocked(false)
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
            .isActive(true)
            .iban("sender_iban")
            .user(users.get(0))
            .build());
        accounts.add(Account.builder()
            .id(2L)
            .accountType(AccountType.CHECKING)
            .balance(2000.00)
            .iban("receiver_iban")
            .isActive(true)
            .user(users.get(1))
            .build()
        );
    }

    @Test
    // happy flow
    void postTransaction() throws AccountNotFoundException, TransactionFailedException {
        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        TransactionResponse result = transactionServiceJpa.postTransaction(transactionRequest, users.get(0));

        // Assert the transaction was successful
        assertNotNull(result);
        assertEquals(900.00, accounts.get(0).getBalance());
        assertEquals(2100.00, accounts.get(1).getBalance());
        assertNotNull(result.getId());
        assertEquals(100.00, result.getAmount());
        assertEquals("sender_iban", result.getSender());
        assertEquals("receiver_iban", result.getReceiver());
        assertEquals(1L, result.getUserPerforming());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void checkIfSenderIsValid() {
    }

    @Test
    void checkIfReceiverIsValid() {
    }

    @Test
    void insufficientFunds() {
    }

    @Test
    void checkIfAccountIsOwnedByUser() {
    }

    @Test
    void checkIfUserIsAuthorized() {
    }
}