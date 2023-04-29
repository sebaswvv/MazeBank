package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.AccountsNotFoundException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceJpa {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) throws UserNotFoundException {
        // get users
        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException("user not found with id: " + id);

        // return the user
        return userRepository.findById(id).orElse(null);
    }

    // patch user by id. Too little difference with getUserById to justify a separate method??
    public User patchUserById(long id) throws UserNotFoundException {
        // get user
        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException("user not found with id: " + id);

        // save user
        userRepository.save(user);

        // return user
        return user;
    }

    public List<UserResponse> getAccountsByUserId(Long userId) throws UserNotFoundException, AccountsNotFoundException {
        // get user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new UserNotFoundException("user not found with id: " + userId);

        // get accounts from user
        List<Account> accounts = user.getAccounts();
        if (accounts == null) throw new AccountsNotFoundException("accounts not found for user with id: " + userId);

        // parse accounts to user responses
        List<UserResponse> userResponses = new ArrayList<>();
        for (Account account : accounts) {
            // Hoe kan ik de iban etc hierin krijgen?
            UserResponse userResponse = UserResponse.builder()
                .id(account.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        // parse users to user responses
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
}