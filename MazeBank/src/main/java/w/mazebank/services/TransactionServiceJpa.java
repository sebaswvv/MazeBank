package w.mazebank.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.*;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceJpa {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AccountServiceJpa accountServiceJpa;

    private final ModelMapper mapper = new ModelMapper();

    private static final String BANK_IBAN = "NL01INHO0000000001";

    public TransactionResponse getTransactionAndValidate(Long id, User userPerforming) throws TransactionNotFoundException {
        Transaction transaction = getTransactionById(id);

        // validate the transaction
        checkIfSenderIsBankAccount(transaction);
        checkIfUserPerformingIsTransactionParticipant(userPerforming, transaction);
        return mapTransactionToResponse(transaction);
    }

    private void checkIfSenderIsBankAccount(Transaction transaction) throws UnauthorizedTransactionAccessException {
        if (transaction.getSender().getIban().equals(BANK_IBAN)) {
            throw new UnauthorizedTransactionAccessException("You are not allowed to access transactions of the bank's bank account");
        }
    }

    private void checkIfUserPerformingIsTransactionParticipant(User user, Transaction transaction) {
        // if the user is an employee, he is allowed to access all transactions
        if (user.getRole() == RoleType.EMPLOYEE) {
            return;
        }
        // if the user is a customer, he is only allowed to access transactions he is a participant of
        if (transaction.getSender().getUser().getId() == user.getId()
            || transaction.getReceiver().getUser().getId() == user.getId()) {
            return;
        }
        throw new UnauthorizedTransactionAccessException("User with id: " + user.getId() + " is not authorized to access transaction with id: " + transaction.getId());
    }

    private TransactionResponse mapTransactionToResponse(Transaction transaction) {
        mapper.typeMap(Transaction.class, TransactionResponse.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getSender().getIban(), TransactionResponse::setSender);
                mapper.map(src -> src.getReceiver().getIban(), TransactionResponse::setReceiver);
            });
        return mapper.map(transaction, TransactionResponse.class);
    }

    @Transactional
    public TransactionResponse postTransaction(TransactionRequest transactionRequest, User userPerforming)
        throws TransactionFailedException, InsufficientFundsException, AccountNotFoundException {

        if (transactionRequest.getSenderIban().equals(BANK_IBAN)) {
            throw new UnauthorizedAccountAccessException("You are not allowed to perform transactions for the bank's bank account");
        }

        // get the 2 accounts involved in the transaction
        Account senderAccount = accountServiceJpa.getAccountByIban(transactionRequest.getSenderIban());
        Account receiverAccount = accountServiceJpa.getAccountByIban(transactionRequest.getReceiverIban());

        // create the transaction
        Transaction transaction = buildTransaction(transactionRequest, userPerforming, senderAccount, receiverAccount);

        validateRegularTransaction(transaction);

        updateAccountBalances(senderAccount, receiverAccount, transactionRequest.getAmount());

        return performTransaction(transaction);
    }

    private Transaction buildTransaction(TransactionRequest request, User userPerforming, Account senderAccount, Account receiverAccount) {
        return Transaction.builder()
            .amount(request.getAmount())
            .description(request.getDescription())
            .transactionType(TransactionType.TRANSFER)
            .userPerforming(userPerforming)
            .sender(senderAccount)
            .receiver(receiverAccount)
            .timestamp(LocalDateTime.now())
            .build();
    }

    private Transaction getTransactionById(Long id) throws TransactionNotFoundException {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction with id: " + id + " not found"));
    }

    private Account getBankAccount() throws AccountNotFoundException {
        return accountServiceJpa.getAccountByIban(BANK_IBAN);
    }

    // used for database seeding
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    private TransactionResponse performTransaction(Transaction transaction) {
        saveTransaction(transaction);

        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getSender().getIban(),
            transaction.getReceiver().getIban(),
            transaction.getUserPerforming().getId(),
            transaction.getTimestamp().toString(),
            transaction.getTransactionType().toString());
    }

    @Transactional
    public TransactionResponse atmAction(Account account, double amount, TransactionType transactionType, User userPerforming)
        throws TransactionFailedException, AccountNotFoundException {

        // create the transaction
        Transaction transaction = Transaction.builder()
            .amount(amount)
            .transactionType(transactionType)
            .userPerforming(userPerforming)
            .sender(getBankAccount())
            .receiver(account)
            .timestamp(java.time.LocalDateTime.now())
            .build();

        validateAtmTransaction(transaction);

        updateAccountBalanceForAtmAction(account, amount, transactionType);

        return performTransaction(transaction);
    }

    // to validate any transaction
    private void validateTransaction(Transaction transaction) throws TransactionFailedException {
        checkIfSenderAndReceiverAreNotTheSame(transaction);
        checkIfOneOfTheAccountsIsBlocked(transaction);
        checkIfTransactionLimitIsExceeded(transaction);
        checkIfAbsoluteLimitIsReached(transaction);
        checkDayLimitExceeded(transaction);
        checkIfSenderIsBlocked(transaction);
    }

    // to validate an atm transaction
    private void validateAtmTransaction(Transaction transaction) throws TransactionFailedException {
        validateTransaction(transaction);

        if (transaction.getReceiver().getAccountType() == AccountType.SAVINGS) {
            throw new TransactionFailedException("Cannot deposit or withdraw to a savings account from an ATM");
        }
    }

    // to validate a regular transaction
    private void validateRegularTransaction(Transaction transaction) throws TransactionFailedException, InsufficientFundsException {
        validateTransaction(transaction);
        savingsAccountCheckSend(transaction);
        checkIfUserIsAuthorized(transaction);
    }

    private void checkIfOneOfTheAccountsIsBlocked(Transaction transaction) throws TransactionFailedException {
        if (!transaction.getSender().isActive())
            throw new TransactionFailedException("Sender account is blocked");

        if (!transaction.getReceiver().isActive())
            throw new TransactionFailedException("Receiver account is blocked");
    }

    private void checkIfSenderIsBlocked(Transaction transaction) throws TransactionFailedException {
        if (transaction.getSender().getUser().isBlocked()) {
            throw new TransactionFailedException("User is blocked");
        }
    }

    private void checkIfUserIsAuthorized(Transaction transaction) throws TransactionFailedException {
        // if the user is an employee, he is allowed to perform any transaction
        // if the user is a customer, he is only allowed to perform transactions on his own accounts
        if (transaction.getUserPerforming().getRole() != RoleType.EMPLOYEE
            && transaction.getUserPerforming().getId() != transaction.getSender().getUser().getId()) {
            throw new TransactionFailedException("User performing the transaction is not authorized to perform this transaction");
        }
    }

    private void checkIfTransactionLimitIsExceeded(Transaction transaction) throws TransactionFailedException {
        // if the transaction amount is higher than the transaction limit of the user
        if (transaction.getAmount() > transaction.getSender().getUser().getTransactionLimit()) {
            throw new TransactionFailedException("Transaction limit exceeded");
        }
    }

    private void checkIfAbsoluteLimitIsReached(Transaction transaction) throws AccountAbsoluteLimitReachedException {
        // Check if the transaction type is either TRANSFER or WITHDRAWAL
        if (transaction.getTransactionType() == TransactionType.TRANSFER
            || transaction.getTransactionType() == TransactionType.WITHDRAWAL) {

            // Determine the account to check based on the transaction type
            Account accountToCheck = transaction.getTransactionType() == TransactionType.WITHDRAWAL
                ? transaction.getReceiver()
                : transaction.getSender();

            double newBalance = accountToCheck.getBalance() - transaction.getAmount();

            if (newBalance < accountToCheck.getAbsoluteLimit()) {
                throw new AccountAbsoluteLimitReachedException("Balance cannot become lower than absolute limit");
            }
        }
    }

    private void checkDayLimitExceeded(Transaction transaction) throws TransactionFailedException {
        Account sender = transaction.getSender();
        Double totalAmountOfTransactionForToday = transactionRepository.getTotalAmountOfTransactionForToday(sender.getId());
        double totalAmountOfToday = totalAmountOfTransactionForToday != null ? totalAmountOfTransactionForToday : 0.0;

        // check if the total amount of today + the amount of the transaction is higher than the day limit
        if (totalAmountOfToday + transaction.getAmount() > sender.getUser().getDayLimit())
            throw new TransactionFailedException("Day limit exceeded");
    }

    private void savingsAccountCheckSend(Transaction transaction) throws TransactionFailedException {
        // if the sender is a savings account and the receiver is not the same customer
        if (transaction.getSender().getAccountType() == AccountType.SAVINGS
            && (transaction.getSender().getUser().getId() != transaction.getReceiver().getUser().getId())) {
            throw new TransactionFailedException("Cannot transfer from a savings account to an account that is not of the same customer");
        }

        // if the receiver is a savings account and the sender is not the same customer
        if (transaction.getReceiver().getAccountType() == AccountType.SAVINGS
            && (transaction.getSender().getUser().getId() != transaction.getReceiver().getUser().getId())) {
            throw new TransactionFailedException("Cannot transfer to a savings account from an account that is not of the same customer");
        }
    }

    private void checkIfSenderAndReceiverAreNotTheSame(Transaction transaction) throws TransactionFailedException {
        if (transaction.getSender().getId() == transaction.getReceiver().getId())
            throw new TransactionFailedException("Sender and receiver cannot be the same");
    }

    private void updateAccountBalances(Account senderAccount, Account receiverAccount, double amount) {
        senderAccount.setBalance(senderAccount.getBalance() - amount);
        receiverAccount.setBalance(receiverAccount.getBalance() + amount);

        // save the updated accounts
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    private void updateAccountBalanceForAtmAction(Account account, double amount, TransactionType transactionType) {
        if (transactionType == TransactionType.WITHDRAWAL) {
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }

        accountRepository.save(account);
    }

    public List<Transaction> getTransactionsByUser(Long accountId, Pageable pageable) {
        return transactionRepository.findBySenderIdOrReceiverId(accountId, accountId, pageable);
    }
}
