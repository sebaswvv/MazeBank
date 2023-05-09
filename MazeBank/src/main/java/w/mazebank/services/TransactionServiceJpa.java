package w.mazebank.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.TransactionFailedException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.exceptions.UnauthorizedTransactionAccessException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.requests.TransactionRequest;
import w.mazebank.models.responses.DepositWithdrawResponse;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

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

    public void transferMoney(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction deposit(Account account, double amount, User userPerforming) {
        // update account balance
        account.setBalance(account.getBalance() + amount);
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

        transactionRepository.save(transaction);

        return transaction;
    }

    public DepositWithdrawResponse createTransaction(TransactionRequest transactionRequest, User userPerforming) throws TransactionFailedException {
        // get sender and receiver accounts
        Account sender = accountServiceJpa.getAccountByIban(transactionRequest.getSenderIban());
        Account receiver = accountServiceJpa.getAccountByIban(transactionRequest.getReceiverIban());

        // check if, after sending the money, the sending account doenst exceed its abosulte limit. In short the account has enough money to send the amount
        if (sender.getBalance() - transactionRequest.getAmount() < sender.getAbsoluteLimit()) {
            throw new TransactionFailedException("Sender would exceed its absolute limit after sending this amount");
        }

        // One cannot directly transfer from a savings account to an account that is not of the same customer
        if (sender.getAccountType() == AccountType.SAVINGS && (sender.getUser().getId() != receiver.getUser().getId())) {
                throw new TransactionFailedException("Cannot transfer from a savings account to an account that is not of the same customer");
        }

        // One cannot directly transfer to a savings account from an account that is not of the same customer.
        if (receiver.getAccountType() == AccountType.SAVINGS && (sender.getUser().getId() != receiver.getUser().getId())) {
                throw new TransactionFailedException("Cannot transfer to a savings account from an account that is not of the same customer");
        }

        // check if daylimit/transactionlimit are not exceeded (voor day limit mischien de transactions van vandaag ophalen en dan de som van de amounts nemen, dan wel de transacties tussen savings en current niet meenemen)
        getTotalAmountOfTransactionsTodayByAccount(sender);

        // check if the userPermoforming is an employee or owns the account from which the money is being sent
        // check if the senders account is not blocked
        // check if the receiver account is not blocked

        // if all checks pass, lower the balance of the sender account and increase the balance of the receiver account
        // create a new transaction and save it to the database
        // make sure that this is all @Transactional
        // return the transaction to the response
        return new DepositWithdrawResponse("test");
    }

    private double getTotalAmountOfTransactionsTodayByAccount(Account account) {
        // double amount =  transactionRepository.getTotalAmountOfTransactionForToday(account);
        // System.out.println(amount);
        System.out.println(transactionRepository.getTotalAmountOfTransactionForToday(account));
        return 0;
    }

}