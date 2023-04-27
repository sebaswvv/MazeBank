package w.mazebank.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.enums.TransactionType;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.repositories.AccountRepository;
import w.mazebank.repositories.TransactionRepository;

@Service
public class TransactionServiceJpa {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

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