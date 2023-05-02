package w.mazebank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.LockedResponse;
import w.mazebank.services.UserServiceJpa;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceJpa userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws UserNotFoundException {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Even opzoeken hoe die requestBody werkt (JsonPatch)
//    @PatchMapping("/{id}")
//    public ResponseEntity<User> patchUserById(@PathVariable long id) throws UserNotFoundException {
//        User user = userService.patchUserById(id);
//        return ResponseEntity.ok(user);
//    }

    // GET/users/{userId}/accounts
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<Object> getAccountsByUserId(@PathVariable Long userId) throws UserNotFoundException, AccountNotFoundException {
        List<AccountResponse> accountResponses = userService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accountResponses);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<LockedResponse> blockUser(@PathVariable Long id) throws UserNotFoundException {
        userService.blockUser(id);
        return ResponseEntity.ok(new LockedResponse(true));
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<LockedResponse> unblockUser(@PathVariable Long id) throws UserNotFoundException {
        userService.unblockUser(id);
        return ResponseEntity.ok(new LockedResponse(true));
    }
}