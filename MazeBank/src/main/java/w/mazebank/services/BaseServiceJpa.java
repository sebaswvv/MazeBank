package w.mazebank.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class BaseServiceJpa {

    public <T> List<T> findAllPaginationAndSort(int offset, int limit, String sort, String search, JpaRepository<T,?> repository) {
        // create sort and pageable object
        Sort sortObject = Sort.by(Sort.Direction.fromString(sort), "id");
        Pageable pageable = PageRequest.of(offset, limit, sortObject);

        // get data
        List<T> results =  repository.findAll(pageable).getContent();

//        // if search is not empty, filter results
//        if (search != null && !search.isEmpty()) {
//            results = filterResultsBySearch(results, search);
//        }

        // return results
        return results;

    }


//    private <T> List<T> filterResultsBySearch(List<T> results, String search) {
//        String[] searchTerms = search.split(",");
//        return results.stream()
//            .filter(result -> {
//                // get fields
//                String[] fields = result.toString().split(",");
//
//                // check if any field contains any of the search terms
//                for (String field : fields) {
//                    for (String searchTerm : searchTerms) {
//                        if (field.toLowerCase().contains(searchTerm.toLowerCase())) {
//                            return true;
//                        }
//                    }
//                }
//
//                // if no field contains any of the search terms, exclude result
//                return false;
//            })
//            .toList();
//    }


}
