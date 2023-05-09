package w.mazebank.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import w.mazebank.enums.AccountType;
import w.mazebank.enums.RoleType;
import w.mazebank.enums.TransactionType;
import w.mazebank.models.Account;
import w.mazebank.models.Transaction;
import w.mazebank.models.User;
import w.mazebank.services.AccountServiceJpa;
import w.mazebank.services.TransactionServiceJpa;
import w.mazebank.services.UserServiceJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements ApplicationRunner {

    @Autowired
    private UserServiceJpa userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountServiceJpa accountService;

    @Autowired
    private TransactionServiceJpa transactionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Create some sample users
        User user1 = new User(1, "user1@example.com", 123456789, "John", "Doe", passwordEncoder.encode("1234"), "1234567890", RoleType.CUSTOMER, LocalDate.now().minusYears(25), LocalDateTime.now(), 1000, 100, false, null);
        User user2 = new User(2, "user2@example.com", 987654321, "Jane", "Smith", passwordEncoder.encode("1234"), "0987654321", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
        User user3 = new User(3, "user3@example.com", 456123789, "Jim", "John", passwordEncoder.encode("1234"), "0987654321", RoleType.EMPLOYEE, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);
        User user4 = new User(4, "user4@example.com", 456123759, "Tim", "Brad", passwordEncoder.encode("1234"), "0987654321", RoleType.CUSTOMER, LocalDate.now().minusYears(30), LocalDateTime.now(), 5000, 200, false, null);

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);

        // Create some sample accounts for user1
        Account account1 = new Account(1, "NL01MAZE0000000001", AccountType.CHECKING, 1000.0, user1, true, LocalDateTime.now(), 1000.0, null, null);
        Account account2 = new Account(2, "NL01MAZE0000000002", AccountType.SAVINGS, 5000.0, user1, true, LocalDateTime.now(), 2000.0, null, null);

        accountService.addAccount(account1);
        accountService.addAccount(account2);

        // Create some sample accounts for user2
        Account account3 = new Account(3, "NL01MAZE0000000003", AccountType.CHECKING, 2000.0, user2, true, LocalDateTime.now(), 1500.0, null, null);
        Account account4 = new Account(4, "NL01MAZE0000000004", AccountType.SAVINGS, 10000.0, user2, true, LocalDateTime.now(), 5000.0, null, null);

        accountService.addAccount(account3);
        accountService.addAccount(account4);

        // Perform some transactions between the accounts
        transactionService.transferMoney(new Transaction(1, "Transfer from account1 to account3", 500.0, account1, account3, TransactionType.TRANSFER, LocalDateTime.now()));
        transactionService.transferMoney(new Transaction(2, "Transfer from account2 to account4", 2000.0, account2, account4, TransactionType.TRANSFER, LocalDateTime.now()));
    }
}