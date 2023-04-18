package W.MazeBank.models;

import W.MazeBank.enums.AccountStatus;
import W.MazeBank.enums.AccountType;

import java.time.LocalDate;

public class Account {

    private long id;
    private String iban;
    private AccountType accountType;
    private double balance;
    private User user; //
    private AccountStatus accountStatus;
    private LocalDate createdAt;
    private double dayLimit;
    private double transactionLimit;
    private double absoluteLimit;
}
