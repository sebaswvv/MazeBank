package w.mazebank.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class BaseServiceJpa {

    public <T> List<T> findAllByPaginationAndSort(int offset, int limit, String sort, String search, JpaRepository<T,?> repository) {
        // create sort and pageable object
        Sort sortObject = Sort.by(Sort.Direction.fromString(sort), "id");
        Pageable pageable = PageRequest.of(offset, limit, sortObject);
        Page<T> page = repository.findAll(pageable);
        return page.getContent();
    }
}
