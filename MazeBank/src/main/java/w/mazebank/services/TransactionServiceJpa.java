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
        checkIfUserIsTransactionParticipant(user, transaction);

        // moddelmapper configuration because the fields dont match
        mapper.typeMap(Transaction.class, TransactionResponse.class)
            .addMappings(mapper -> {
                mapper.map(src -> src.getSender().getIban(), TransactionResponse::setSender);
                mapper.map(src -> src.getReceiver().getIban(), TransactionResponse::setReceiver);
            });

        // map the transaction to a transaction response
        return mapper.map(transaction, TransactionResponse.class);
    }

    @Transactional
    public TransactionResponse postTransaction(TransactionRequest transactionRequest, User userPerforming)
        throws TransactionFailedException, InsufficientFundsException {
        Account senderAccount = accountServiceJpa.getAccountByIban(transactionRequest.getSenderIban());
        Account receiverAccount = accountServiceJpa.getAccountByIban(transactionRequest.getReceiverIban());

        // update account balance sender and receiver
        senderAccount.setBalance(senderAccount.getBalance() - transactionRequest.getAmount());
        accountRepository.save(senderAccount);
        receiverAccount.setBalance(receiverAccount.getBalance() + transactionRequest.getAmount());
        accountRepository.save(receiverAccount);

        // create transaction from the transaction request
        Transaction transaction = Transaction.builder()
            .amount(transactionRequest.getAmount())
            .description(transactionRequest.getDescription())
            .transactionType(TransactionType.TRANSFER)
            .userPerforming(userPerforming)
            .sender(senderAccount)
            .receiver(receiverAccount)
            .timestamp(LocalDateTime.now())
            .build();

        validateRegularTransaction(transaction);

        return performTransaction(transaction);
    }

    private Transaction getTransactionById(Long id) throws TransactionNotFoundException {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if (transaction == null) throw new TransactionNotFoundException("Transaction with id: " + id + " not found");
        return transaction;
    }

    private void checkIfUserIsTransactionParticipant(User user, Transaction transaction) {
        // allow if user is of type employee
        if(user.getRole() == RoleType.EMPLOYEE) return;

        // if user is sender or receiver of transaction, allow
        if(transaction.getSender().getUser().getId() == user.getId()
            || transaction.getReceiver().getUser().getId() == user.getId()) return;

        // else throw exception
        throw new UnauthorizedTransactionAccessException("User with id: " + user.getId() + " is not authorized to access transaction with id: " + transaction.getId());
    }

    // return the bankaccount of the Bank
    private Account getBankAccount() {
        return accountRepository.findAll().get(0);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    // add transaction to the dataabase
    private TransactionResponse performTransaction(Transaction transaction){
        // save transaction to the db
        transactionRepository.save(transaction);

        // return transaction response
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getSender().getIban(),
            transaction.getReceiver().getIban(),
            transaction.getUserPerforming().getId(),
            transaction.getTimestamp(),
            transaction.getTransactionType().toString());
    }

    // method for both deposit and withdrawal
    @Transactional
    public TransactionResponse atmAction(Account account, double amount, TransactionType transactionType, User userPerforming) throws TransactionFailedException {

        // update account balance depending on transaction type
        if(transactionType == TransactionType.WITHDRAWAL){
            account.setBalance(account.getBalance() - amount);
        }
        else if(transactionType == TransactionType.DEPOSIT){
            account.setBalance(account.getBalance() + amount);
        }

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

        // validate transaction
        validateAtmTransaction(transaction);

        return performTransaction(transaction);
    }

    // validation for all types of transactions
    private void validateTransaction(Transaction transaction) throws TransactionFailedException {
        checkIfSenderAndReceiverAreNotTheSame(transaction);
        checkIfAnAccountIsBlocked(transaction);
        checkIfTransactionLimitIsExceeded(transaction);
        validateSufficientFunds(transaction);
        checkDayLimitExceeded(transaction);
        checkIfUserIsBlocked(transaction);
    }

    // validation for all types of ATM transactions
    private void validateAtmTransaction(Transaction transaction) throws TransactionFailedException {
        // validate transaction
        validateTransaction(transaction);

        // check if receiver is a savings account
        if(transaction.getReceiver().getAccountType() == AccountType.SAVINGS)
            throw new TransactionFailedException("Cannot deposit or withdraw to a savings account from an ATM");
    }

    // DEZE NOG EVEN LATEN STAAN VOOR ALS ER NIEUWE VALIDATIES MOETEN WORDEN TOEGEVOEGD
    // private void validateDepositTransaction(Transaction transaction) throws TransactionFailedException {
    //     validateAtmTransaction(transaction);
    // }
    //
    // private void validateWithdrawalTransaction(Transaction transaction) throws TransactionFailedException {
    //     validateAtmTransaction(transaction);
    // }


    private void validateRegularTransaction(Transaction transaction)
        throws TransactionFailedException, InsufficientFundsException {
        validateTransaction(transaction);

        // One cannot directly transfer from a savings account to an account that is not of the same customer
        // One cannot directly transfer to a savings account from an account that is not of the same customer.
        savingsAccountCheckSend(transaction);

        // check if the user is an employee or owns the account from which the money is being sent
        checkUser(transaction);
    }

    private void checkIfAnAccountIsBlocked(Transaction transaction) throws TransactionFailedException {
        if(!transaction.getSender().isActive())
            throw new TransactionFailedException("Sender account is blocked");

        if(!transaction.getReceiver().isActive())
            throw new TransactionFailedException("Receiver account is blocked");
    }

    private void checkIfUserIsBlocked(Transaction transaction) throws TransactionFailedException {
        // get User from sender account
        User user = transaction.getSender().getUser();

        if(!user.isBlocked())
            throw new TransactionFailedException("User is blocked");
    }

    private void checkUser(Transaction transaction) throws TransactionFailedException {
        if(transaction.getUserPerforming().getRole() != RoleType.EMPLOYEE
            && transaction.getUserPerforming().getId() != transaction.getSender().getUser().getId())
            throw new TransactionFailedException("User performing the transaction is not authorized to perform this transaction");
    }

    private void checkIfTransactionLimitIsExceeded(Transaction transaction) throws TransactionFailedException {
        if(transaction.getAmount() > transaction.getSender().getUser().getTransactionLimit())
            throw new TransactionFailedException("Transaction limit exceeded");
    }

    private void checkDayLimitExceeded(Transaction transaction) throws TransactionFailedException {
        if (dayLimitExceeded(transaction.getSender(), transaction.getAmount()))
            throw new TransactionFailedException("Day limit exceeded");
    }

    private void savingsAccountCheckSend(Transaction transaction) throws TransactionFailedException {
        if(transaction.getSender().getAccountType() == AccountType.SAVINGS && (transaction.getSender().getUser().getId() != transaction.getReceiver().getUser().getId()))
            throw new TransactionFailedException("Cannot transfer from a savings account to an account that is not of the same customer");

        if(transaction.getReceiver().getAccountType() == AccountType.SAVINGS && (transaction.getSender().getUser().getId() != transaction.getReceiver().getUser().getId()))
            throw new TransactionFailedException("Cannot transfer to a savings account from an account that is not of the same customer");
    }

    private void validateSufficientFunds(Transaction transaction) throws InsufficientFundsException {
        // TODO: moet dit niet met absolute limit?

        if (transaction.getSender().getBalance() - transaction.getAmount() < 0)
            throw new InsufficientFundsException("Sender has insufficient funds");
    }

    private void checkIfSenderAndReceiverAreNotTheSame(Transaction transaction) throws TransactionFailedException {
        if(transaction.getSender().getId() == transaction.getReceiver().getId())
            throw new TransactionFailedException("Sender and receiver cannot be the same");
    }

    private boolean dayLimitExceeded(Account sender, double amount) {
        // save in Double object for the NULL checking
        Double totalAmountOfTransactionForToday =  transactionRepository.getTotalAmountOfTransactionForToday(sender.getId());
        double currentTotal = totalAmountOfTransactionForToday != null ? totalAmountOfTransactionForToday : 0.0;

        // check if the day limit is exceeded
        double dayLimit = sender.getUser().getDayLimit();
        return currentTotal + amount > dayLimit;
    }
}
