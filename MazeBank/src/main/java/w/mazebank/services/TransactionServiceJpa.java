package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.models.Transaction;
import w.mazebank.repositories.TransactionRepository;

@Service
public class TransactionServiceJpa {
    @Autowired
    private TransactionRepository transactionRepository;

    public void transferMoney(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}