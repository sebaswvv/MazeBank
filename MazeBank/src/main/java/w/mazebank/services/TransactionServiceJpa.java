package w.mazebank.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.InsufficientFundsException;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.exceptions.UnauthorizedTransactionAccessException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.AtmResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

import java.time.LocalDateTime;

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

    public TransactionResponse getTransactionAndValidate(Long id, User user) throws TransactionNotFoundException {
        // get transaction by id
        Transaction transaction = getTransactionById(id);

        // check if the user is allowed to access the transaction
        validateTransactionParticipant(user, transaction);

        // moddelmapper configuration because the fields dont match
        mapper.typeMap(Transaction.class, TransactionResponse.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getSender().getIban(), TransactionResponse::setSender);
                mapper.map(src -> src.getReceiver().getIban(), TransactionResponse::setReceiver);
            });

        // map the transaction to a transaction response
        return mapper.map(transaction, TransactionResponse.class);
    }

    public Transaction getTransactionById(Long id) throws TransactionNotFoundException {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null) throw new TransactionNotFoundException("Transaction with id: " + id + " not found");
        return transaction;
    }

    private void validateTransactionParticipant(User user, Transaction transaction) {
        // allow if user is of type employee
        if(user.getRole() == RoleType.EMPLOYEE) return;

        // if user is sender or receiver of transaction, allow
        if(transaction.getSender().getUser().getId() == user.getId()
            || transaction.getReceiver().getUser().getId() == user.getId()) return;

        // else throw exception
        throw new UnauthorizedTransactionAccessException("User with id: " + user.getId() + " is not authorized to access transaction with id: " + transaction.getId());
    }

    private Account getBankAccount() {
        return accountRepository.findAll().get(0);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }



    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest, User userPerforming)
        throws TransactionFailedException, InsufficientFundsException {
        Account senderAccount = accountServiceJpa.getAccountByIban(transactionRequest.getSenderIban());
        Account receiverAccount = accountServiceJpa.getAccountByIban(transactionRequest.getReceiverIban());

        validateTransaction(transactionRequest, senderAccount, receiverAccount, userPerforming);

        // perform the transaction
        Transaction transaction = performTransaction(transactionRequest, senderAccount, receiverAccount, userPerforming);

        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getDescription(),
            senderAccount.getIban(),
            receiverAccount.getIban(),
            userPerforming.getId(),
            LocalDateTime.now(),
            transaction.getTransactionType().toString());
    }

    private Transaction performTransaction(TransactionRequest transactionRequest, Account sender, Account receiver, User userPerforming) {
        // update account balance sender
        sender.setBalance(sender.getBalance() - transactionRequest.getAmount());
        accountRepository.save(sender);

        // update balance receiver
        receiver.setBalance(receiver.getBalance() + transactionRequest.getAmount());
        accountRepository.save(receiver);

        // save transaction
        Transaction transaction = Transaction.builder()
            .amount(transactionRequest.getAmount())
            .transactionType(TransactionType.TRANSFER)
            .userPerforming(userPerforming)
            .sender(sender)
            .receiver(receiver)
            .timestamp(LocalDateTime.now())
            .build();

        return transactionRepository.save(transaction);
    }

    private void validateTransaction(TransactionRequest transactionRequest, Account sender, Account receiver, User userPerforming)
        throws TransactionFailedException, InsufficientFundsException {
        // check if the sender and receiver are not the same
        checkIfSenderAndReceiverAreNotTheSame(sender, receiver);

        // check if, after sending the money, the sending account doesn't exceed its absolute limit.
        validateSufficientFunds(transactionRequest, sender);

        // One cannot directly transfer from a savings account to an account that is not of the same customer
        // One cannot directly transfer to a savings account from an account that is not of the same customer.
        savingsAccountCheckSend(sender, receiver);

        // check if day limit are not exceeded
        checkDayLimitExceeded(transactionRequest, sender);

        // check transaction limit is not exceeded
        checkIfTransactionLimitIsExceeded(transactionRequest, sender);

        // check if the user is an employee or owns the account from which the money is being sent
        checkUser(sender, userPerforming);

        // check if the sender and receiver are not blocked
        checkIfAnAccountIsBlocked(sender, receiver);
    }

    private void checkIfAnAccountIsBlocked(Account sender, Account receiver) throws TransactionFailedException {
        // check if the senders account is not blocked
        if (!sender.isActive())
            throw new TransactionFailedException("Sender account is blocked");

        // check if the receiver account is not blocked
        if (!receiver.isActive())
            throw new TransactionFailedException("Receiver account is blocked");
    }

    private void checkUser(Account sender, User userPerforming) throws TransactionFailedException {
        if (userPerforming.getRole() != RoleType.EMPLOYEE && userPerforming.getId() != sender.getUser().getId())
            throw new TransactionFailedException("User performing the transaction is not authorized to perform this transaction");
    }

    private void checkIfTransactionLimitIsExceeded(TransactionRequest transactionRequest, Account sender) throws TransactionFailedException {
        if (transactionRequest.getAmount() > sender.getUser().getTransactionLimit())
            throw new TransactionFailedException("Transaction limit exceeded");
    }

    private void checkDayLimitExceeded(TransactionRequest transactionRequest, Account sender) throws TransactionFailedException {
        if (dayLimitExceeded(sender, transactionRequest.getAmount()))
            throw new TransactionFailedException("Day limit exceeded");
    }

    private void savingsAccountCheckSend(Account sender, Account receiver) throws TransactionFailedException {
        if (sender.getAccountType() == AccountType.SAVINGS && (sender.getUser().getId() != receiver.getUser().getId()))
            throw new TransactionFailedException("Cannot transfer from a savings account to an account that is not of the same customer");

        if (receiver.getAccountType() == AccountType.SAVINGS && (sender.getUser().getId() != receiver.getUser().getId()))
            throw new TransactionFailedException("Cannot transfer to a savings account from an account that is not of the same customer");
    }

    private void validateSufficientFunds(TransactionRequest transactionRequest, Account sender) throws InsufficientFundsException {
        if (sender.getBalance() - transactionRequest.getAmount() < sender.getAbsoluteLimit())
            throw new InsufficientFundsException("Sender would exceed it's absolute limit after sending this amount");
    }

    private void checkIfSenderAndReceiverAreNotTheSame(Account sender, Account receiver) throws TransactionFailedException {
        if (sender.getId() == receiver.getId())
            throw new TransactionFailedException("Sender and receiver cannot be the same");
    }

    private boolean dayLimitExceeded(Account sender, double amount) {
        double totalAmountOfTransactionForToday =  transactionRepository.getTotalAmountOfTransactionForToday(sender.getId());

        // check if the day limit is exceeded
        double dayLimit = sender.getUser().getDayLimit();
        return totalAmountOfTransactionForToday + amount > dayLimit;
    }

    @Transactional
    public Transaction atmAction(Account account, double amount, TransactionType transactionType, User userPerforming){

        // update account balance depending on transaction type
        if(transactionType == TransactionType.WITHDRAWAL)
            account.setBalance(account.getBalance() - amount);
        else if(transactionType == TransactionType.DEPOSIT)
            account.setBalance(account.getBalance() + amount);

        // save account's new balance
        accountRepository.save(account);

        // create transaction of type deposit and save it
        Transaction transaction = Transaction.builder()
            .amount(amount)
            .transactionType(TransactionType.DEPOSIT)
            .userPerforming(userPerforming)
            .sender(getBankAccount())
            .receiver(account)
            .timestamp(java.time.LocalDateTime.now())
            .build();

        return transactionRepository.save(transaction);
    }


}
