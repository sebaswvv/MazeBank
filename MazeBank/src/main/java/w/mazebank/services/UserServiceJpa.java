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

    private Specification<Transaction> specification = Specification.where(null);

    public User getUserById(Long id) throws UserNotFoundException {
        checkIfUserIsNotTheBank(id);
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("user not found with id: " + id));
    }

    public User getUserByIdAndValidate(Long id, User userPerforming) throws UserNotFoundException {
        checkIfUserIsNotTheBank(id);

        // check if user id is the same as the user performing the request or if the user performing the request is an employee and not blocked
        if (userPerforming.getId() != id && (!userPerforming.getRole().equals(RoleType.EMPLOYEE) || userPerforming.isBlocked())) {
            throw new UserNotFoundException("user not found with id: " + id);
        }
        return getUserById(id);
    }

    private void checkIfUserIsNotTheBank(Long userId) {
        if (userId == 1) throw new UnauthorizedUserAccessException("You are not allowed to access the bank");
    }

    private void checkIfUserIsAllowedToAccessAccount(Long userId, User userPerforming) {
        // throw exception if user is not an employee and not the user performing the request
        if (!userPerforming.getRole().equals(RoleType.EMPLOYEE) && userPerforming.getId() != userId) {
            throw new UnauthorizedAccountAccessException("user not allowed to access accounts of user with id: " + userId);
        }
    }

    public List<AccountResponse> getAccountsByUserId(Long userId, User userPerforming) throws UserNotFoundException, UnauthorizedAccountAccessException {
        checkIfUserIsNotTheBank(userId);
        checkIfUserIsAllowedToAccessAccount(userId, userPerforming);

        // get user
        User user = getUserById(userId);

        // get accounts from user
        List<Account> accounts = user.getAccounts();
        if (accounts == null) return new ArrayList<>();

        return buildAccountResponse(accounts);
    }

    private List<AccountResponse> buildAccountResponse(List<Account> accounts) {
        // parse accounts to account responses
        List<AccountResponse> accountResponses = new ArrayList<>();
        for (Account account : accounts) {
            AccountResponse accountResponse = AccountResponse.builder()
                .id(account.getId())
                .accountType(account.getAccountType().getValue())
                .iban(account.getIban())
                .balance(account.getBalance())
                .timestamp(account.getCreatedAt().toString())
                .build();
            accountResponses.add(accountResponse);
        }
        // return account responses
        return accountResponses;
    }

    public List<UserResponse> getAllUsers(int pageNumber, int pageSize, String sort, String search, boolean withoutAccounts) {
        List<User> users = findAllPaginationAndSort(pageNumber, pageSize, sort, search, userRepository);

        List<User> filteredUsers = new ArrayList<>(users);

        // If withoutAccounts is true, remove users that have accounts
        if (withoutAccounts) {
            filteredUsers.removeIf(user -> user.getAccounts() != null && !user.getAccounts().isEmpty());
        }

        return buildUserResponse(filteredUsers);
    }

    private List<UserResponse> buildUserResponse(List<User> filteredUsers) {
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
        checkIfUserIsNotTheBank(id);

        User user = getUserById(id);
        user.setBlocked(true);

        userRepository.save(user);
    }

    public void unblockUser(Long id) throws UserNotFoundException {
        checkIfUserIsNotTheBank(id);

        User user = getUserById(id);
        user.setBlocked(false);

        userRepository.save(user);
    }

    public User patchUserById(long id, UserPatchRequest userPatchRequest, User userPerforming) throws UserNotFoundException, DisallowedFieldException {
        checkIfUserIsNotTheBank(id);
        User userToPatch = getUserById(id);
        if (userPerforming.getId() != id && !userPerforming.getRole().equals(RoleType.EMPLOYEE)) {
            throw new UnauthorizedUserAccessException("user not allowed to access user with id: " + id);
        }

        checkAllowedFields(userPatchRequest);

        patchesAllowedForCustomer(userPatchRequest, userToPatch);
        patchesAllowedForEmployee(userPatchRequest, userPerforming, userToPatch);

        userRepository.save(userToPatch);

        return userToPatch;
    }

    private void patchesAllowedForEmployee(UserPatchRequest userPatchRequest, User userPerforming, User userToPatch) {
        // PATCHES AVAILABLE FOR EMPLOYEES
        if ((userPatchRequest.getTransactionLimit() != null || userPatchRequest.getDayLimit() != null) && userPerforming.getRole() != RoleType.EMPLOYEE) {
            throw new UnauthorizedUserAccessException("You are not allowed to update the transaction limit or day limit");
        }

        if (userPatchRequest.getTransactionLimit() != null && userPerforming.getRole() == RoleType.EMPLOYEE)
            userToPatch.setTransactionLimit(userPatchRequest.getTransactionLimit());
        if (userPatchRequest.getDayLimit() != null && userPerforming.getRole() == RoleType.EMPLOYEE)
            userToPatch.setDayLimit(userPatchRequest.getDayLimit());
    }

    private void patchesAllowedForCustomer(UserPatchRequest userPatchRequest, User userToPatch) {
        // PATCHES AVAILABLE FOR CUSTOMERS
        if (userPatchRequest.getEmail() != null) userToPatch.setEmail(userPatchRequest.getEmail());
        if (userPatchRequest.getFirstName() != null) userToPatch.setFirstName(userPatchRequest.getFirstName());
        if (userPatchRequest.getLastName() != null) userToPatch.setLastName(userPatchRequest.getLastName());
        if (userPatchRequest.getPhoneNumber() != null) userToPatch.setPhoneNumber(userPatchRequest.getPhoneNumber());
    }

    private void checkAllowedFields(UserPatchRequest userPatchRequest) {
        List<String> allowedFields = Arrays.asList("email", "firstName", "lastName", "phoneNumber");

        // check if fields are allowed
        for (String field : userPatchRequest.getFields()) {
            if (!allowedFields.contains(field))
                throw new DisallowedFieldException("field not allowed to update: " + field);
        }
    }

    public void deleteUserById(Long id)
        throws UserNotFoundException, UserHasAccountsException {
        checkIfUserIsNotTheBank(id);
        User user = getUserById(id);

        checkIfUserHasAccounts(user);

        userRepository.delete(user);
    }

    private void checkIfUserHasAccounts(User user) throws UserHasAccountsException {
        // if user has accounts, cannot delete user
        if (user.getAccounts() != null && !user.getAccounts().isEmpty())
            throw new UserHasAccountsException("user has accounts, cannot delete user");
    }


    public List<TransactionResponse> getTransactionsByUserId(Long userId, User user, int pageNumber, int pageSize, String sort, String fromIban, String toIban, LocalDate startDate, LocalDate endDate, Double maxAmount, Double minAmount, Double amount
    ) throws UserNotFoundException {
        User requestedUser = getUserByIdAndValidate(userId, user);

        buildQueryFromParameters(fromIban, toIban, startDate, endDate, maxAmount, minAmount, amount, requestedUser);

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, "timestamp"));

        Page<Transaction> transactionPage = transactionRepository.findAll(specification, pageable);
        List<Transaction> transactions = transactionPage != null ? transactionPage.getContent() : Collections.emptyList();

        return mapTransactionsToResponses(transactions);
    }

    private void buildQueryFromParameters(String fromIban, String toIban, LocalDate startDate, LocalDate endDate, Double maxAmount, Double minAmount, Double amount, User requestedUser) {
        this.specification = Specification.where(null);
        addFromIbanCondition(fromIban);
        addToIbanCondition(toIban);
        addStartDateCondition(startDate);
        addEndDateCondition(endDate);
        addMaxAmountCondition(maxAmount);
        addMinAmountCondition(minAmount);
        addAmountCondition(amount);
        addRequestedUserCondition(requestedUser);
    }

    private void addFromIbanCondition(String fromIban) {
        if (fromIban != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("sender").get("iban")),
                    "%" + fromIban.toLowerCase() + "%"
                )
            );
        }
    }

    private void addToIbanCondition(String toIban) {
        if (toIban != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("receiver").get("iban")),
                    "%" + toIban.toLowerCase() + "%"
                )
            );
        }
    }

    private void addStartDateCondition(LocalDate startDate) {
        if (startDate != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate.atStartOfDay())
            );
        }
    }

    private void addEndDateCondition(LocalDate endDate) {
        if (endDate != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate.atTime(LocalTime.MAX))
            );
        }
    }

    private void addMaxAmountCondition(Double maxAmount) {
        if (maxAmount != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount)
            );
        }
    }

    private void addMinAmountCondition(Double minAmount) {
        if (minAmount != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount)
            );
        }
    }

    private void addAmountCondition(Double amount) {
        if (amount != null) {
            this.specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("amount"), amount)
            );
        }
    }

    private void addRequestedUserCondition(User requestedUser) {
        List<Account> userAccounts = requestedUser.getAccounts();

        this.specification = specification.and((root, query, criteriaBuilder) ->
            criteriaBuilder.or(
                root.get("sender").in(userAccounts),
                root.get("receiver").in(userAccounts)
            )
        );
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
        checkIfUserIsNotTheBank(userId);

        // check if userId is from a user that is a existing user
        // and validate if the performing user has the rights to access the user
        User user = getUserByIdAndValidate(userId, userPerforming);
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setUserId(userId);

        return getBalanceResponse(user, balanceResponse);
    }

    private BalanceResponse getBalanceResponse(User user, BalanceResponse balanceResponse) {
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

    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("user not found"));
    }
}
