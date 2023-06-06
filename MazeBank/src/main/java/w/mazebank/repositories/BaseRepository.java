package w.mazebank.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID, J> extends JpaRepository<T, ID> {
    List<T> findBySearchString(String search, Pageable pageable);
}