package w.mazebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.sender = ?1 AND t.timestamp = CURRENT_DATE")
    Double getTotalAmountOfTransactionForToday(Account account);

    // Double getTotalAmountOfTransactionForTodayBySender(Account account);
}
