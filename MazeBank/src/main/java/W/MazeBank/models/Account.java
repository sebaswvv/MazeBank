package W.MazeBank.models;

import W.MazeBank.enums.AccountStatus;
import W.MazeBank.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
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
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.ORDINAL)
    private AccountStatus accountStatus;

    private LocalDate createdAt;

    private double dayLimit;

    private double transactionLimit;

    private double absoluteLimit;

    @OneToMany(mappedBy="sender")
    @JsonBackReference
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy="receiver")
    @JsonBackReference
    private List<Transaction> receivedTransactions;

    protected Account() {}

    public Account(long id, String iban, AccountType accountType, double balance, User user, AccountStatus accountStatus, LocalDate createdAt, double dayLimit, double transactionLimit, double absoluteLimit) {
        this.id = id;
        this.iban = iban;
        this.accountType = accountType;
        this.balance = balance;
        this.user = user;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.dayLimit = dayLimit;
        this.transactionLimit = transactionLimit;
        this.absoluteLimit = absoluteLimit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public double getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(double dayLimit) {
        this.dayLimit = dayLimit;
    }

    public double getTransactionLimit() {
        return transactionLimit;
    }

    public void setTransactionLimit(double transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    public double getAbsoluteLimit() {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(double absoluteLimit) {
        this.absoluteLimit = absoluteLimit;
    }

    public List<Transaction> getSentTransactions() {
        return sentTransactions;
    }

    public void setSentTransactions(List<Transaction> sentTransactions) {
        this.sentTransactions = sentTransactions;
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    public void setReceivedTransactions(List<Transaction> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }
}
