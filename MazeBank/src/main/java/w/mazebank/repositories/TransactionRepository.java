package w.mazebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}