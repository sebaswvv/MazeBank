package w.mazebank.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long, JpaSpecificationExecutor<Transaction>> {
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
    List<Transaction> findBySearchString(@Param("iban") String search, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.sender.user.id = :sender OR t.receiver.user.id = :receiver)")
    List<Transaction> findBySenderUserIdOrReceiverUserId(@Param("sender") Long senderId, @Param("receiver") Long receiverId, Pageable pageable);

    List<Transaction> findBySenderIdOrReceiverId(Long senderIban, Long receiverIban, Pageable pageable);

    Page<Transaction> findAll(Specification<Transaction> specification, Pageable pageable);





//     LUC

//     List<Transaction> findBySenderUserAndAmountBetweenAndTimestampBetween(User user, Double minAmount, Double maxAmount, LocalDateTime startDate, LocalDateTime endDate);
//
//     List<Transaction> findBySenderUserAndAmountAndTimestampBetween(User user, Double amount, LocalDateTime startDate, LocalDateTime endDate);
//
//     List<Transaction> findBySenderUserAndAmountGreaterThanEqualAndTimestampBetween(User user, Double minAmount, LocalDateTime startDate, LocalDateTime endDate);
//
//     List<Transaction> findBySenderUserAndAmountLessThanEqualAndTimestampBetween(User user, Double maxAmount, LocalDateTime startDate, LocalDateTime endDate);
//
//     List<Transaction> findBySenderUser(User user);
//
//     List<Transaction> findBySenderUserOrReceiverUserAndSenderIbanOrReceiverIban(User senderUser, User receiverUser, String senderIban, String receiverIban);
}


