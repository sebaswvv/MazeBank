package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.Account;
import w.mazebank.models.User;
import w.mazebank.models.responses.AccountResponse;
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
        return user;
    }

//    // patch user by id. Too little difference with getUserById to justify a separate method??
//    public User patchUserById(long id) throws UserNotFoundException {
//        // get user
//        User user = getUserById(id);
//
//
//        // save user
//        userRepository.save(user);
//
//        // return user
//        return user;
//    }

    public List<AccountResponse> getAccountsByUserId(Long userId) throws UserNotFoundException, AccountNotFoundException {
        // get user
        User user = getUserById(userId);

        // get accounts from user
        List<Account> accounts = user.getAccounts();
        if (accounts == null) throw new AccountNotFoundException("accounts not found for user with id: " + userId);

        // parse accounts to account responses
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (Account account : accounts) {
            AccountResponse accountResponse = AccountResponse.builder()
                .id(account.getId())
                .iban(account.getIban())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
            accountResponses.add(accountResponse);
        }

        // return account responses
        return accountResponses;

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

    public void blockUser(Long id) throws UserNotFoundException {
        // TODO: check if request is done by employee, if not throw NotAuthException??

        User user = getUserById(id);
        user.setBlocked(true);

        userRepository.save(user);
    }

    public void unblockUser(Long id) throws UserNotFoundException {
        // TODO: check if request is done by employee, if not throw NotAuthException??

        User user = getUserById(id);
        user.setBlocked(false);

        userRepository.save(user);
    }
}