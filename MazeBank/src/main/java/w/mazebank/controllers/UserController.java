package w.mazebank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.DisallowedFieldException;
import w.mazebank.exceptions.UserHasAccountsException;
import w.mazebank.exceptions.UserNotFoundException;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.models.responses.*;
import w.mazebank.services.UserServiceJpa;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceJpa userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @AuthenticationPrincipal User userPerforming) throws UserNotFoundException {
        User user = userService.getUserByIdAndValidate(id, userPerforming);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUserById(@PathVariable long id, @RequestBody UserPatchRequest userPatchRequest, @AuthenticationPrincipal User userPerforming) throws UserNotFoundException, DisallowedFieldException {
        User user = userService.patchUserById(id, userPatchRequest, userPerforming);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<UserDeletedResponse> deleteUserById(@PathVariable Long id) throws UserHasAccountsException, UserNotFoundException {
        userService.deleteUserById(id);
        return ResponseEntity.ok(new UserDeletedResponse("User with id: " + id + " was deleted successfully"));
    }

    // GET/users/{userId}/accounts
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<Object> getAccountsByUserId(@PathVariable Long userId, @AuthenticationPrincipal User user) throws UserNotFoundException, AccountNotFoundException {
        List<AccountResponse> accountResponses = userService.getAccountsByUserId(userId, user);
        return ResponseEntity.ok(accountResponses);
    }

    @GetMapping
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<List<UserResponse>> getAllUsers(
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "asc") String sort,
        @RequestParam(required = false) String search)
    {
        List<UserResponse> users = userService.getAllUsers(offset, limit, sort, search);
        return ResponseEntity.ok(users);
    }


    @PutMapping("/{id}/disable")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<LockedResponse> blockUser(@PathVariable Long id) throws UserNotFoundException {
        userService.blockUser(id);
        return ResponseEntity.ok(new LockedResponse(true));
    }

    @PutMapping("/{id}/enable")
    @Secured("ROLE_EMPLOYEE")
    public ResponseEntity<LockedResponse> unblockUser(@PathVariable Long id) throws UserNotFoundException {
        userService.unblockUser(id);
        return ResponseEntity.ok(new LockedResponse(false));
    }

    // GET/users/{userId}/transactions
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(
        @PathVariable Long userId,
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "asc") String sort,
        @RequestParam(required = false) String search)
        throws UserNotFoundException
    {
        List<TransactionResponse> transactionResponses = userService.getTransactionsByUserId(userId, user, offset, limit, sort, search);
        return ResponseEntity.ok(transactionResponses);
    }
}