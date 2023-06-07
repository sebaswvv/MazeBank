package w.mazebank.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.TransactionRepository;
import w.mazebank.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {
    @InjectMocks
    private UserServiceJpa userServiceJpa;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionServiceJpa transactionServiceJpa;

    @Test
    void getUserByIdThatDoesNotExist() {
        // mock the findById method and return null
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.getUserById(2L);
        });
        assertEquals("user not found with id: 2", exception.getMessage());
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .build());
        users.add(User.builder()
            .id(3L)
            .firstName("Jane")
            .lastName("Doe")
            .build()
        );

        Sort sortObject = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sortObject);
        Page<User> usersPage = new PageImpl<>(users);

        // mock the findAll method and return users in a page
        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // call the method
        List<UserResponse> results = userServiceJpa.getAllUsers(0, 10, "asc", "", false);

        // test results
        assertEquals(2, results.size());

        assertEquals(2L, results.get(0).getId());
        assertEquals("John", results.get(0).getFirstName());
        assertEquals("Doe", results.get(0).getLastName());

        assertEquals(3L, results.get(1).getId());
        assertEquals("Jane", results.get(1).getFirstName());
        assertEquals("Doe", results.get(1).getLastName());
    }

    @Test
    void getAllUsersThatHaveNoAccounts() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .accounts(List.of(
                Account.builder()
                    .id(1L)
                    .accountType(AccountType.CHECKING)
                    .build()
            ))
            .build());
        users.add(User.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .build()
        );

        Sort sortObject = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sortObject);
        Page<User> usersPage = new PageImpl<>(users);

        // mock the findAll method and return users in a page
        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // call the method
        List<UserResponse> results = userServiceJpa.getAllUsers(0, 10, "asc", "", true);

        // test results
        assertEquals(1, results.size());

        assertEquals(2L, results.get(0).getId());
        assertEquals("Jane", results.get(0).getFirstName());
        assertEquals("Doe", results.get(0).getLastName());
    }

    @Test
    void blockUser() throws UserNotFoundException {
        // create regular non-blocked user
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .blocked(false)
            .build();

        // mock the findById method and return blocked user
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // call the method
        userServiceJpa.blockUser(2L);

        // test results
        assertEquals(true, user.isBlocked());
        verify(userRepository).save(user);
    }

    @Test
    void unblockUser() throws UserNotFoundException {
        // create blocked user
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .blocked(true)
            .build();

        // mock the findById method and return blocked user
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // call the method
        userServiceJpa.unblockUser(2L);

        // test results
        assertEquals(false, user.isBlocked());
        verify(userRepository).save(user);
    }

    @Test
    void blockNonExistingUser() {
        // mock the findById method and return null
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // check if usernotfoundexception is thrown
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.blockUser(2L);
        });

        // test results
        assertEquals("user not found with id: 2", exception.getMessage());
        verify(userRepository).findById(2L);
    }

    @Test
    void unblockNonExistingUser() {
        // mock the findById method and return null
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // check if usernotfoundexception is thrown
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.unblockUser(2L);
        });

        // test results
        assertEquals("user not found with id: 2", exception.getMessage());
        verify(userRepository).findById(2L);
    }


    @Test
    void addUser() {
        // create a user
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // mock the repository
        when(userRepository.save(user)).thenReturn(user);

        // call the method
        userServiceJpa.addUser(user);

        // test
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUserByIdThatHasAccounts() {
        // create a user
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // add an account to the user to mock the user having accounts
        user.setAccounts(List.of(
            Account.builder()
                .id(2L)
                .balance(100.0)
                .user(user)
                .build()
        ));

        // mock the repository
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // call the method
        Exception exception = assertThrows(UserHasAccountsException.class, () -> {
            // call the method
            userServiceJpa.deleteUserById(2L);
        });

        // test for the exception and verify the method was not called
        assertEquals("user has accounts, cannot delete user", exception.getMessage());
        verify(userRepository, times(0)).delete(user);
    }

    // @Test
    // void addUser() {
    //
    // }
    //
    // @Test
    // void patchUserById() {
    // }

    @Test
    void getAccountsByUserIdWithAccountOwner() throws UserNotFoundException {
        // create a user
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // create two accounts for the user
        Account account1 = Account.builder()
            .id(2L)
            .iban("NL01INHO0000000002")
            .balance(100.0)
            .accountType(AccountType.CHECKING)
            .user(user)
            .build();
        Account account2 = Account.builder()
            .id(3L)
            .balance(200.0)
            .iban("NL01INHO0000000003")
            .accountType(AccountType.SAVINGS)
            .user(user)
            .build();

        user.setAccounts(List.of(account1, account2));

        // mock the repository
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // call the method
        List<AccountResponse> accounts = userServiceJpa.getAccountsByUserId(2L, user);

        // test results
        assertEquals(2, accounts.size());
        assertEquals("NL01INHO0000000002", accounts.get(0).getIban());
        assertEquals("NL01INHO0000000003", accounts.get(1).getIban());
    }

    @Test
    void getAccountsByUserIdWithEmployee() throws UserNotFoundException {
        // create employee for performing user
        User employee = User.builder()
            .id(3L)
            .firstName("Jane")
            .lastName("Doe")
            .role(RoleType.EMPLOYEE)
            .build();

        // create a user for the bank accounts
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // create two accounts for the user
        Account account1 = Account.builder()
            .id(1L)
            .iban("NL01INHO0000000002")
            .balance(100.0)
            .accountType(AccountType.CHECKING)
            .user(user)
            .build();
        Account account2 = Account.builder()
            .id(2L)
            .balance(200.0)
            .iban("NL01INHO0000000003")
            .accountType(AccountType.SAVINGS)
            .user(user)
            .build();

        user.setAccounts(List.of(account1, account2));

        // mock the repository
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // call the method
        List<AccountResponse> accounts = userServiceJpa.getAccountsByUserId(2L, employee);

        // test results
        assertEquals(2, accounts.size());
        assertEquals("NL01INHO0000000002", accounts.get(0).getIban());
        assertEquals("NL01INHO0000000003", accounts.get(1).getIban());
    }

    @Test
    void getAccountsByUserUnauthorized() throws UnauthorizedAccountAccessException {
        // create a user
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        User performingUser = User.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .build();

        //should get UnauthorizedAccountAccessException
        Exception exception = assertThrows(UnauthorizedUserAccessException.class, () -> {
            // call the method
            userServiceJpa.getAccountsByUserId(1L, performingUser);
        });

        // test results
        assertEquals("You are not allowed to access the bank", exception.getMessage());
        verify(userRepository, times(0)).findById(1L);
    }

    @Test
    void getAccountsByUserIdWithNoExistingUser() {
        // create a user
        User user = User.builder()
            .id(3L)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.EMPLOYEE)
            .build();

        // mock the repository
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        //should get UserNotFoundException
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.getAccountsByUserId(2L, user);
        });

        // test results
        assertEquals("user not found with id: 2", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void getTransactionsByUserId() throws UserNotFoundException, TransactionNotFoundException, TransactionFailedException, AccountNotFoundException {
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // create two accounts for the user
        Account account1 = Account.builder()
            .id(1L)
            .iban("NL01INHO0000000002")
            .balance(100.0)
            .accountType(AccountType.CHECKING)
            .user(user)
            .build();
        Account account2 = Account.builder()
            .id(2L)
            .balance(200.0)
            .iban("NL01INHO0000000003")
            .accountType(AccountType.SAVINGS)
            .user(user)
            .build();

        // create two transactions for the user
        Transaction transaction1 = Transaction.builder()
            .id(1L)
            .description("test transaction 1")
            .amount(100.0)
            .userPerforming(user)
            .sender(account1)
            .receiver(account2)
            .transactionType(TransactionType.TRANSFER)
            .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0))
            .build();
        Transaction transaction2 = Transaction.builder()
            .id(2L)
            .description("test transaction 2")
            .amount(200.0)
            .userPerforming(user)
            .sender(account2)
            .receiver(account1)
            .transactionType(TransactionType.TRANSFER)
            .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0))
            .build();

        TransactionResponse transactionResponse1 = TransactionResponse.builder()
            .id(1L)
            .description("test transaction 1")
            .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0).toString())
            .amount(100.0)
            .sender("NL01INHO0000000002")
            .receiver("NL01INHO0000000003")
            .build();

        TransactionResponse transactionResponse2 = TransactionResponse.builder()
            .id(2L)
            .description("test transaction 2")
            .timestamp(LocalDateTime.of(2021, 1, 1, 0, 0).toString())
            .amount(200.0)
            .sender("NL01INHO0000000003")
            .receiver("NL01INHO0000000002")
            .build();


        // Mock the repository
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(transaction1, transaction2)));


        // Call the method
        List<TransactionResponse> transactions = userServiceJpa.getTransactionsByUserId(2L, user, 0, 10, "Asc", null, null, null, null, null, null, null);

        // Test results
        assertEquals(2, transactions.size());
        assertEquals(transactionResponse1, transactions.get(0));
        assertEquals(transactionResponse2, transactions.get(1));
    }


    @Test
    void getTransactionsByUserIdWithNoExistingUser() {
        // create a user
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.EMPLOYEE)
            .build();

        // mock the repository
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        List<Transaction> transactions = new ArrayList<>();

        //should get UserNotFoundException
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.getTransactionsByUserId(2L, user, 0, 10, "Asc", null, null, null, null, null, null, null);
        });

        // test results
        assertEquals("user not found with id: 2", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void patchUserHappyFlowCustomer() throws UserNotFoundException {
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@gmail.com")
            .role(RoleType.CUSTOMER)
            .phoneNumber("0612345678")
            .build();
        UserPatchRequest requestBody = UserPatchRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("janedoe@gmail.com")
            .phoneNumber("0687654321")
            .build();

        // mock the findById method and return an account
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // call the method
        User result = userServiceJpa.patchUserById(2L, requestBody, user);

        // test results
        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("janedoe@gmail.com", result.getEmail());
        assertEquals("0687654321", result.getPhoneNumber());
    }

    @Test
    void patchUserHappyFlowEmployee() throws UserNotFoundException {
        User performingUser = User.builder()
            .id(3L)
            .firstName("Bill")
            .lastName("Gates")
            .role(RoleType.EMPLOYEE)
            .build();
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@gmail.com")
            .role(RoleType.CUSTOMER)
            .phoneNumber("0612345678")
            .dayLimit(1000.0)
            .transactionLimit(100.0)
            .build();
        UserPatchRequest requestBody = UserPatchRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("janedoe@gmail.com")
            .phoneNumber("0687654321")
            .build();

        // mock the findById method and return an account
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // call the method
        User result = userServiceJpa.patchUserById(2L, requestBody, performingUser);

        // test results
        assertEquals(2L, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("janedoe@gmail.com", result.getEmail());
        assertEquals("0687654321", result.getPhoneNumber());
    }

    @Test
    void patchUserThrowsUserNotFound() {
        User performingUser = User.builder()
            .id(3L)
            .firstName("Bill")
            .lastName("Gates")
            .role(RoleType.EMPLOYEE)
            .build();
        UserPatchRequest requestBody = new UserPatchRequest();

        // mock the findById method and return null
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userServiceJpa.patchUserById(2L, requestBody, performingUser);
        });
    }

    @Test
    void patchUserThrowsUnauthorizedUserAccess() {
        User performingUser = User.builder()
            .id(3L)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .build();
        User user = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .email("johndoe@gmail.com")
            .role(RoleType.CUSTOMER)
            .build();
        UserPatchRequest requestBody = new UserPatchRequest();

        // mock the findById method and return null
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));

        // test results
        UnauthorizedUserAccessException exception = assertThrows(UnauthorizedUserAccessException.class, () -> {
            userServiceJpa.patchUserById(2L, requestBody, performingUser);
        });
        assertEquals("user not allowed to access user with id: 2", exception.getMessage());
    }

    @Test
    void patchUserThrowsUnauthorizedUserAccessForTheBanksUser() {
        User performingUser = User.builder()
            .id(2L)
            .firstName("John")
            .lastName("Doe")
            .role(RoleType.CUSTOMER)
            .build();
        UserPatchRequest requestBody = new UserPatchRequest();

        // test results
        UnauthorizedUserAccessException exception = assertThrows(UnauthorizedUserAccessException.class, () -> {
            userServiceJpa.patchUserById(1L, requestBody, performingUser);
        });
        assertEquals("You are not allowed to access the bank", exception.getMessage());
    }
}