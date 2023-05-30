package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import w.mazebank.enums.RoleType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceJpa extends BaseServiceJpa {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("user not found with id: " + id));
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

    public List<AccountResponse> getAccountsByUserId(Long userId, User userPerforming) throws UserNotFoundException, UnauthorizedAccountAccessException {
        // throw exception if user is not an employee and not the user performing the request
        if(!userPerforming.getRole().equals(RoleType.EMPLOYEE) && userPerforming.getId() != userId) {
            throw new UnauthorizedAccountAccessException("user not allowed to access accounts of user with id: " + userId);
        }

        // get user
        User user = getUserById(userId);

        // get accounts from user
        List<Account> accounts = user.getAccounts();
        if (accounts == null) return new ArrayList<>();

        // parse accounts to account responses
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (Account account : accounts) {
            AccountResponse accountResponse = AccountResponse.builder()
                .id(account.getId())
                .accountType(account.getAccountType().getValue())
                .iban(account.getIban())
                .balance(account.getBalance())
                .timestamp(account.getCreatedAt())
                .build();
            accountResponses.add(accountResponse);
        }
        // return account responses
        return accountResponses;

    }

    public List<UserResponse> getAllUsers(int offset, int limit, String sort, String search) {
        List<User> users = findAllPaginationAndSort(offset, limit, sort, search, userRepository);

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

    public User patchUserById(long id, UserPatchRequest userPatchRequest) throws UserNotFoundException, DisallowedFieldException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException("user not found with id: " + id);

        List<String> allowedFields = Arrays.asList("email", "firstName", "lastName", "phoneNumber", "dayLimit", "transactionLimit");

        // check if fields are allowed
        for (String field : userPatchRequest.getFields()) {
            if (!allowedFields.contains(field)) throw new DisallowedFieldException("field not allowed to update: " + field);
        }

        if (userPatchRequest.getEmail() != null) user.setEmail(userPatchRequest.getEmail());
        if (userPatchRequest.getFirstName() != null) user.setFirstName(userPatchRequest.getFirstName());
        if (userPatchRequest.getLastName() != null) user.setLastName(userPatchRequest.getLastName());
        if (userPatchRequest.getPhoneNumber() != null) user.setPhoneNumber(userPatchRequest.getPhoneNumber());
        if (userPatchRequest.getDayLimit() != null) user.setDayLimit(userPatchRequest.getDayLimit());
        if (userPatchRequest.getTransactionLimit() != null) user.setTransactionLimit(userPatchRequest.getTransactionLimit());

        userRepository.save(user);

        return user;
    }

    public void deleteUserById(Long id)
        throws UserNotFoundException, UserHasAccountsException {
        User user = getUserById(id);

        // if user has accounts, cannot delete user
        if (user.getAccounts() != null && !user.getAccounts().isEmpty())
            throw new UserHasAccountsException("user has accounts, cannot delete user");

        userRepository.delete(user);
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId, User userPerforming, int offset, int limit, String sort, String search) throws UserNotFoundException, UnauthorizedAccountAccessException {

        if (userPerforming.getRole() != RoleType.EMPLOYEE && userPerforming.getId() != userId) {
            throw new UnauthorizedAccountAccessException("user not allowed to access transactions of user with id: " + userId);
        }

        // can you make pageable with offset, limit, sort, search?
        Sort sortObject = Sort.by(Sort.Direction.fromString(sort), "id");
        Pageable pageable = PageRequest.of(offset, limit, sortObject);

        List<Transaction> transactions = transactionRepository.findBySenderUserIdOrReceiverUserId(userId, userId, pageable);

        // make transaction into transaction response
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .sender(transaction.getSender().getIban())
                .receiver(transaction.getReceiver().getIban())
                .timestamp(transaction.getTimestamp())
                .build();
            transactionResponses.add(transactionResponse);
        }

        return transactionResponses;
    }
}