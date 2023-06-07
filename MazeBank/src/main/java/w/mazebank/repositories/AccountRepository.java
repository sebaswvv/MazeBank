package w.mazebank.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import w.mazebank.models.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long, JpaSpecificationExecutor<Account>> {
    Optional<Account> findByIban(String iban);

    @Override
    @Query("SELECT a FROM Account a WHERE a.iban LIKE %?1%")
    List<Account> findBySearchString(String search, Pageable pageable);

    @Query("""
            SELECT a FROM Account a
            WHERE
               LOWER(a.user.firstName) LIKE LOWER(CONCAT('%', :name,'%')) OR
               LOWER(a.user.lastName) LIKE LOWER(CONCAT('%', :name,'%'))
        """)
    List<Account> findAccountsByOneName(@Param("name") String name);

    @Query("""
            SELECT a FROM Account a
            WHERE
                LOWER(a.user.firstName) LIKE LOWER(CONCAT('%', :firstName,'%')) AND
                LOWER(a.user.lastName) LIKE LOWER(CONCAT('%', :lastName,'%'))
        """)
    List<Account> findAccountsByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Override
    @Query("SELECT a FROM Account a WHERE a.iban <> 'NL01INHO0000000001'")
    Page<Account> findAll(Pageable pageable);
}