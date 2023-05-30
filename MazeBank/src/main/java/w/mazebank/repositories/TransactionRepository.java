package w.mazebank.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    @Query("SELECT CAST(ROUND(SUM(t.amount), 2) AS DOUBLE) as total " +
        "FROM Transaction t " +
        "JOIN t.sender s " +
        "JOIN t.receiver r " +
        "JOIN s.user u1 " +
        "JOIN r.user u2 " +
        "WHERE CAST(t.timestamp AS DATE) = CURRENT_DATE() " +
        "  AND s.id = :senderId " +
        "  AND u1.id <> u2.id")
    Double getTotalAmountOfTransactionForToday(@Param("senderId") Long senderId);

    @Override
    @Query("SELECT t FROM Transaction t WHERE t.sender.iban LIKE %?1% OR t.receiver.iban LIKE %?1%")
    List<Transaction> findBySearchString(String search, Pageable pageable);

    List<Transaction> findBySenderUserIdOrReceiverUserId(Long senderId, Long receiverId, Pageable pageable);
}
