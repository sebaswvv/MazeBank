package W.MazeBank.models;

import W.MazeBank.enums.TransactionType;
import jakarta.annotation.Nullable;

import java.time.LocalDate;

public class Transaction {
    private long id;
    private String description;
    private double amount;
    @Nullable
    private Account sender;
    @Nullable
    private Account receiver;
    private LocalDate createdAt;
    private TransactionType transactionType;
}
