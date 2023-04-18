package W.MazeBank.models;

import W.MazeBank.enums.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    private double amount;

    @Nullable
    @ManyToOne
    @JoinColumn(name="sender_id")
    private Account sender;

    @Nullable
    @ManyToOne
    @JoinColumn(name="receiver_id")
    private Account receiver;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;

    private LocalDate createdAt;

    protected Transaction() {}

    public Transaction(long id, String description, double amount, Account sender, Account receiver, TransactionType transactionType, LocalDate createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.sender = sender;
        this.receiver = receiver;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
