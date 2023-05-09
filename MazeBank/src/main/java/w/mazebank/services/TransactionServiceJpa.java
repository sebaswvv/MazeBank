package w.mazebank.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.exceptions.AccountNotFoundException;
import w.mazebank.exceptions.TransactionNotFoundException;
import w.mazebank.exceptions.UnauthorizedAccountAccessException;
import w.mazebank.exceptions.UnauthorizedTransactionAccessException;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.models.responses.TransactionResponse;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

@Service
public class TransactionServiceJpa {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

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



    public void transferMoney(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction deposit(Account account, double amount) {
        // update account balance
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        // create transaction of type deposit and save it
        Transaction transaction = Transaction.builder()
            .amount(amount)
            .transactionType(TransactionType.DEPOSIT)
            .sender(null)
            .receiver(account)
            .createdAt(java.time.LocalDateTime.now())
            .build();

        transactionRepository.save(transaction);

        return transaction;
    }
}