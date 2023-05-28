package w.mazebank.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import w.mazebank.exceptions.UserHasAccountsException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.UserRepository;

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
    void deleteUserById() throws UserNotFoundException, UserHasAccountsException {
        // create a user
        User user = User.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .build();

        // mock the repository
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        doNothing().when(userRepository).delete(user);


        // call the method
        userServiceJpa.deleteUserById(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void addUser() {

    }

    @Test
    void patchUserById() {
    }
}