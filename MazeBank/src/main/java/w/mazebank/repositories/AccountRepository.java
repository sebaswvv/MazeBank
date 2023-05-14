package w.mazebank.repositories;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;
    import w.mazebank.models.Account;

    import java.util.List;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Account findByIban(String iban);

    @Override
    @Query("SELECT a FROM Account a WHERE a.iban LIKE %?1%")
    List<Account> findBySearchString(String search, Pageable pageable);
}