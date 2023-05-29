package w.mazebank.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.UserHasAccountsException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaTest {
    @InjectMocks
    private UserServiceJpa userServiceJpa;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUserByIdThatDoesNotExist() {
        // mock the findById method and return null
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.getUserById(1L);
        });
        assertEquals("user not found with id: 1", exception.getMessage());
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
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

        Sort sortObject = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sortObject);
        Page<User> usersPage = new PageImpl<>(users);

        // mock the findAll method and return users in a page
        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        // call the method
        List<UserResponse> results = userServiceJpa.getAllUsers(0, 10, "asc", "");

        // test results
        assertEquals(2, results.size());

        assertEquals(1L, results.get(0).getId());
        assertEquals("John", results.get(0).getFirstName());
        assertEquals("Doe", results.get(0).getLastName());

        assertEquals(2L, results.get(1).getId());
        assertEquals("Jane", results.get(1).getFirstName());
        assertEquals("Doe", results.get(1).getLastName());
    }

    @Test
    void blockUser() throws UserNotFoundException {
        // create regular non-blocked user
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .blocked(false)
            .build();

        // mock the findById method and return blocked user
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // call the method
        userServiceJpa.blockUser(1L);

        // test results
        assertEquals(true, user.isBlocked());
        verify(userRepository).save(user);
    }

    @Test
    void blockNonExistingUser() {
        // mock the findById method and return null
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // check if usernotfoundexception is thrown
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            // call the method
            userServiceJpa.blockUser(1L);
        });

        // test results
        assertEquals("user not found with id: 1", exception.getMessage());
        verify(userRepository).findById(1L);
    }

    @Test
    void addUser() {

        // test results
        // assertEquals("user not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteUserByIdThatHasAccounts(){
        // create a user
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // add an account to the user to mock the user having accounts
        user.setAccounts(List.of(
            Account.builder()
                .id(1L)
                .balance(100.0)
                .user(user)
                .build()
        ));

        // mock the repository
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        // call the method
        Exception exception = assertThrows(UserHasAccountsException.class, () -> {
            // call the method
            userServiceJpa.deleteUserById(1L);
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
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // create two accounts for the user
        Account account1 = Account.builder()
            .id(1L)
            .iban("NL01INHO0000000001")
            .balance(100.0)
            .accountType(AccountType.CHECKING)
            .user(user)
            .build();
        Account account2 = Account.builder()
            .id(2L)
            .balance(200.0)
            .iban("NL01INHO0000000002")
            .accountType(AccountType.SAVINGS)
            .user(user)
            .build();

        user.setAccounts(List.of(account1, account2));

        // mock the repository
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        // call the method
        List<AccountResponse> accounts = userServiceJpa.getAccountsByUserId(1L, user);

        // test results
        assertEquals(2, accounts.size());
        assertEquals("NL01INHO0000000001", accounts.get(0).getIban());
        assertEquals("NL01INHO0000000002", accounts.get(1).getIban());

    }

    @Test
    void getAccountsByUserIdWithEmployee() throws UserNotFoundException {
        // create employee for performing user
        User employee = User.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .role(RoleType.EMPLOYEE)
            .build();

        // create a user for the bank accounts
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // create two accounts for the user
        Account account1 = Account.builder()
            .id(1L)
            .iban("NL01INHO0000000001")
            .balance(100.0)
            .accountType(AccountType.CHECKING)
            .user(user)
            .build();
        Account account2 = Account.builder()
            .id(2L)
            .balance(200.0)
            .iban("NL01INHO0000000002")
            .accountType(AccountType.SAVINGS)
            .user(user)
            .build();

        user.setAccounts(List.of(account1, account2));

        // mock the repository
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        // call the method
        List<AccountResponse> accounts = userServiceJpa.getAccountsByUserId(1L, employee);

        // test results
        assertEquals(2, accounts.size());
        assertEquals("NL01INHO0000000001", accounts.get(0).getIban());
        assertEquals("NL01INHO0000000002", accounts.get(1).getIban());
    }
}