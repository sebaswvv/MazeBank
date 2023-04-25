package w.mazebank.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import w.mazebank.enums.AccountType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String iban;

    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean isActive;

    private LocalDateTime createdAt;

    private double dayLimit;

    private double transactionLimit;

    private double absoluteLimit;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Transaction> receivedTransactions;
}