package w.mazebank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.mazebank.models.Account;
import w.mazebank.repositories.AccountRepository;

@Service
public class AccountServiceJpa {
    @Autowired
    private AccountRepository accountRepository;

    public void addAccount(Account account) {
        accountRepository.save(account);
    }
}