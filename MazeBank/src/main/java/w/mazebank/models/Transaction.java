package w.mazebank.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import w.mazebank.enums.TransactionType;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_performing_id")
    private User userPerforming;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "sender_id")
    private Account sender;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "receiver_id")
    private Account receiver;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;

    private LocalDateTime timestamp;
}