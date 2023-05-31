package w.mazebank.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.InsufficientFundsException;
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
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .blocked(false)
            .build());
        users.add(User.builder()
            .id(3L)
            .blocked(false)
            .firstName("Jane")
            .lastName("Doe")
            .build()
        );
        users.add(User.builder()
            .id(4L)
            .blocked(false)
            .firstName("Jack")
            .lastName("Doe")
            .build());
        users.add(User.builder()
            .id(5L)
            .blocked(false)
            .role(RoleType.EMPLOYEE)
            .firstName("Jill")
            .lastName("Doe")
            .build());


        // create two accounts
        accounts = new ArrayList<>();
        accounts.add(Account.builder()
            .id(2L)
            .accountType(AccountType.CHECKING)
            .balance(1000.00)
            .isActive(true)
            .iban("sender_iban")
            .user(users.get(0))
            .build());
        accounts.add(Account.builder()
            .id(3L)
            .accountType(AccountType.CHECKING)
            .balance(2000.00)
            .iban("receiver_iban")
            .isActive(true)
            .user(users.get(1))
            .build()
        );
        accounts.add(Account.builder()
            .id(4L)
            .accountType(AccountType.SAVINGS)
            .balance(1000.00)
            .iban("savings_iban")
            .isActive(true)
            .user(users.get(0))
            .build()
        );
        // bank account
        accounts.add(Account.builder()
            .id(5L)
            .accountType(AccountType.CHECKING)
            .balance(100000.00)
            .iban("bank_iban")
            .isActive(true)
            .user(users.get(3))
            .build()
        );
    }

    @Test
    void deposit() throws AccountNotFoundException, TransactionFailedException {
        // mock the accountServiceJpa.getAccountByIban
        when(accountServiceJpa.getAccountByIban(Mockito.any())).thenReturn(accounts.get(3));

        // Perform the transaction
        TransactionResponse result = transactionServiceJpa.atmAction(accounts.get(0), 100.00, TransactionType.DEPOSIT, users.get(0));

        // Assert the transaction was successful
        assertNotNull(result);
        assertEquals(1100.00, accounts.get(0).getBalance());
    }

    @Test
    void withdraw() throws AccountNotFoundException, TransactionFailedException {
        // mock the accountServiceJpa.getAccountByIban
        when(accountServiceJpa.getAccountByIban(Mockito.any())).thenReturn(accounts.get(3));

        // Perform the transaction
        TransactionResponse result = transactionServiceJpa.atmAction(accounts.get(1), 100.00, TransactionType.WITHDRAWAL, users.get(1));

        // Assert the transaction was successful
        assertNotNull(result);
        assertEquals(1900.00, accounts.get(1).getBalance());
    }

    @Test
    void ATMActionReceiverCannotBeSavings() throws AccountNotFoundException {
        // mock the accountServiceJpa.getAccountByIban
        when(accountServiceJpa.getAccountByIban(Mockito.any())).thenReturn(accounts.get(3));

        // Create atm action, for account id 3. This is a savings account. This throw TransactionFailedException
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.atmAction(accounts.get(2), 100.00, TransactionType.DEPOSIT, users.get(0)));
    }

    @Test
    void userCannotBeBlocked() throws AccountNotFoundException {
        // block user of account 1
        users.get(0).setBlocked(true);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));

        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void accountCannotBeBlocked() throws AccountNotFoundException {
        // block account 1
        accounts.get(0).setActive(false);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));

        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void cannotSendFromSavingsToCheckingFromDifferentUsers() throws AccountNotFoundException {
        // account 3 to account 2 should not work and throw an TransactionFailedException
        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("savings_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("savings_iban")).thenReturn(accounts.get(2));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void senderAndReceiverCannotBeTheSame() throws AccountNotFoundException {
        // account 1 to account 1 should not work and throw an TransactionFailedException
        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("sender_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void dayLimitCannotBeExceeded() throws AccountNotFoundException {
        // account 1 (sender), set day limit to 1000
        accounts.get(0).getUser().setDayLimit(1000.00);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(1001.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(InsufficientFundsException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));

        // Assert the transaction was not successful
        assertEquals(0, transactionRepository.findAll().size());
    }


    @Test
    void transactionLimitCannotBeExceeded() throws AccountNotFoundException {
        // account 1 (sender), set transaction limit to 1000
        accounts.get(0).getUser().setTransactionLimit(1000.00);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(1001.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> transactionServiceJpa.postTransaction(transactionRequest, users.get(0)));

        // Assert the transaction was not successful
        assertEquals(0, transactionRepository.findAll().size());
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
        assertEquals(2L, result.getUserPerforming());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void insufficientFunds() throws AccountNotFoundException {
        // set the sender's balance to 0
        accounts.get(0).setBalance(0.00);

        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(InsufficientFundsException.class, () -> {
            transactionServiceJpa.postTransaction(transactionRequest, users.get(0));
        });

        // check if the transaction was not saved
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void accountIsNotOwnedByUserPerforming() throws AccountNotFoundException {
        // user 3 tries to send money from user 1's account
        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction
        assertThrows(TransactionFailedException.class, () -> {
            transactionServiceJpa.postTransaction(transactionRequest, users.get(2));
        });

        // check if the transaction was not saved
        assertEquals(0, transactionRepository.findAll().size());
    }

    @Test
    void checkIfUserIsAuthorized() throws AccountNotFoundException, TransactionFailedException {
        // user 4 is employee and should be authorized
        // Create a transaction request
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setSenderIban("sender_iban");
        transactionRequest.setReceiverIban("receiver_iban");
        transactionRequest.setAmount(100.00);

        // Mock the behavior of accountServiceJpa
        when(accountServiceJpa.getAccountByIban("sender_iban")).thenReturn(accounts.get(0));
        when(accountServiceJpa.getAccountByIban("receiver_iban")).thenReturn(accounts.get(1));

        // Perform the transaction, should be successful
        TransactionResponse result = transactionServiceJpa.postTransaction(transactionRequest, users.get(3));

        // Assert the transaction was successful
        assertNotNull(result);
        assertEquals(900.00, accounts.get(0).getBalance());
        assertEquals(2100.00, accounts.get(1).getBalance());
        assertNotNull(result.getId());
        assertEquals(100.00, result.getAmount());
        assertEquals("sender_iban", result.getSender());
        assertEquals("receiver_iban", result.getReceiver());
        assertEquals(5L, result.getUserPerforming());
        assertNotNull(result.getTimestamp());
    }
}