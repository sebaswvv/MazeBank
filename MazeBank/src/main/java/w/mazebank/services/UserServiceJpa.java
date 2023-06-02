package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.UserPatchRequest;
import w.mazebank.models.responses.AccountResponse;
import w.mazebank.models.responses.BalanceResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.models.responses.UserResponse;
import w.mazebank.repositories.TransactionRepository;
import w.mazebank.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class UserServiceJpa extends BaseServiceJpa {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public User getUserById(Long id) throws UserNotFoundException {
        if(id == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");
        else
            return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("user not found with id: " + id));
    }

    public User getUserByIdAndValidate(Long id, User userPerforming) throws UserNotFoundException {
        if(id == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        // check if user id is the same as the user performing the request or if the user performing the request is an employee and not blocked
        if (userPerforming.getId() != id && (!userPerforming.getRole().equals(RoleType.EMPLOYEE) || userPerforming.isBlocked())) {
            throw new UserNotFoundException("user not found with id: " + id);
        }
        return getUserById(id);
    }


    public List<AccountResponse> getAccountsByUserId(Long userId, User userPerforming) throws UserNotFoundException, UnauthorizedAccountAccessException {
        if(userId == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        // throw exception if user is not an employee and not the user performing the request
        if (!userPerforming.getRole().equals(RoleType.EMPLOYEE) && userPerforming.getId() != userId) {
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

    public List<UserResponse> getAllUsers(int offset, int limit, String sort, String search, boolean withoutAccounts) {
        List<User> users = findAllPaginationAndSort(offset, limit, sort, search, userRepository);

        List<User> filteredUsers = new ArrayList<>(users);

        // remove the user account of the bank
        filteredUsers.removeIf(user -> user.getId() == 1L);

        // If withoutAccounts is true, remove users that have accounts
        if (withoutAccounts) {
            filteredUsers.removeIf(user -> user.getAccounts() != null && !user.getAccounts().isEmpty());
        }

        // Parse users to user responses
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : filteredUsers) {
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
        if (id == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        User user = getUserById(id);
        user.setBlocked(true);

        userRepository.save(user);
    }

    public void unblockUser(Long id) throws UserNotFoundException {
        if (id == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        User user = getUserById(id);
        user.setBlocked(false);

        userRepository.save(user);
    }

    public User patchUserById(long id, UserPatchRequest userPatchRequest, User userPerforming) throws UserNotFoundException, DisallowedFieldException {
        if(id == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        User user = userRepository.findById(id).orElse(null);
        if (user == null) throw new UserNotFoundException("user not found with id: " + id);

        // check if user is the same as the user performing the request or if the user performing the request is an employee
        if (userPerforming.getId() != id && !userPerforming.getRole().equals(RoleType.EMPLOYEE)) {
            throw new UnauthorizedUserAccessException("user not allowed to access user with id: " + id);
        }

        List<String> allowedFields = Arrays.asList("email", "firstName", "lastName", "phoneNumber", "dayLimit", "transactionLimit");

        // check if fields are allowed
        for (String field : userPatchRequest.getFields()) {
            if (!allowedFields.contains(field))
                throw new DisallowedFieldException("field not allowed to update: " + field);
        }

        if (userPatchRequest.getEmail() != null) user.setEmail(userPatchRequest.getEmail());
        if (userPatchRequest.getFirstName() != null) user.setFirstName(userPatchRequest.getFirstName());
        if (userPatchRequest.getLastName() != null) user.setLastName(userPatchRequest.getLastName());
        if (userPatchRequest.getPhoneNumber() != null) user.setPhoneNumber(userPatchRequest.getPhoneNumber());
        if (userPatchRequest.getDayLimit() != null) user.setDayLimit(userPatchRequest.getDayLimit());
        if (userPatchRequest.getTransactionLimit() != null)
            user.setTransactionLimit(userPatchRequest.getTransactionLimit());

        userRepository.save(user);

        return user;
    }

    public void deleteUserById(Long id)
        throws UserNotFoundException, UserHasAccountsException {
        if (id == 1) throw new UnauthorizedUserAccessException("You are not allowed to delete the bank");
        User user = getUserById(id);

        // if user has accounts, cannot delete user
        if (user.getAccounts() != null && !user.getAccounts().isEmpty())
            throw new UserHasAccountsException("user has accounts, cannot delete user");

        userRepository.delete(user);
    }


    public List<TransactionResponse> getTransactionsByUserId(
        Long userId,
        User user,
        int offset,
        int limit,
        String sort,
        String search,
        LocalDate startDate,
        LocalDate endDate,
        Double maxAmount,
        Double minAmount,
        Double amount
    ) throws UserNotFoundException {
        User requestedUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

        Specification<Transaction> specification = Specification.where(null);

        // if (search != null) {
        //     specification = specification.and((root, query, criteriaBuilder) ->
        //         criteriaBuilder.like(
        //             criteriaBuilder.lower(root.get("search")),
        //             "%" + search.toLowerCase() + "%"
        //         )
        //     );
        // }

        if (search != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("sender").get("iban")),
                        "%" + search.toLowerCase() + "%"
                    ),
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("receiver").get("iban")),
                        "%" + search.toLowerCase() + "%"
                    )
                )
            );
        }

        if (startDate != null && endDate != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("timestamp"), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX))
            );
        }

        if (maxAmount != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount)
            );
        }

        if (minAmount != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount)
            );
        }

        if (amount != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("amount"), amount)
            );
        }

        specification = specification.and((root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                criteriaBuilder.equal(root.get("sender"), requestedUser),
                criteriaBuilder.equal(root.get("receiver"), requestedUser)
            )
        );

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(direction, "timestamp"));

        Page<Transaction> transactionPage = transactionRepository.findAll(specification, pageable);
        List<Transaction> transactions = transactionPage.getContent();

        return mapTransactionsToResponses(transactions);
    }

    private List<TransactionResponse> mapTransactionsToResponses(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .sender(transaction.getSender().getIban())
                .receiver(transaction.getReceiver().getIban())
                .timestamp(transaction.getTimestamp().toString())
                .build();
            transactionResponses.add(transactionResponse);
        }
        return transactionResponses;
    }

    public BalanceResponse getBalanceByUserId(Long userId, User userPerforming) throws UserNotFoundException {
        if (userId == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");

        // check if userId is from a user that is a existing user
        // and validate if the performing user has the rights to access the user
        User user = getUserByIdAndValidate(userId, userPerforming);
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setUserId(userId);

        // calculate total balance and set checking and savings balance if account exists
        for (Account account : user.getAccounts()) {
            if (account.getAccountType() == AccountType.CHECKING) {
                balanceResponse.setCheckingBalance(account.getBalance());
            } else if (account.getAccountType() == AccountType.SAVINGS) {
                balanceResponse.setSavingsBalance(account.getBalance());
            }
            // add balance to total balance
            balanceResponse.setTotalBalance(balanceResponse.getTotalBalance() + account.getBalance());
        }

        return balanceResponse;
    }
}
