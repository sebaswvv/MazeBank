package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.repositories.UserRepository;

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

    public void addUser(User user) {
        userRepository.save(user);
    }
}