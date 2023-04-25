package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.UserNotFoundException;
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