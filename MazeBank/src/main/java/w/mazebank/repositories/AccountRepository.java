package w.mazebank.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}