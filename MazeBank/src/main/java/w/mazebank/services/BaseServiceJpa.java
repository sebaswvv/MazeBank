package w.mazebank.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import w.mazebank.repositories.BaseRepository;

import java.util.List;

public class BaseServiceJpa {
    public <T> List<T> findAllPaginationAndSort(int offset, int limit, String sort, String search, BaseRepository<T, ?, JpaSpecificationExecutor<T>> repository) {
        // create sort and pageable object
        Sort sortObject = Sort.by(Sort.Direction.fromString(sort), "id");
        Pageable pageable = PageRequest.of(offset, limit, sortObject);

        List<T> results;

        // if search string is not empty or null
        if (search != null && !search.isEmpty()) {
            results = repository.findBySearchString(search, pageable);
        } else {
            results = repository.findAll(pageable).getContent();
        }

        // return results
        return results;
    }
}
