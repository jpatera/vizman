package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Caly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

//@Transactional(readOnly = true)
public interface CalyRepo extends JpaRepository<Caly, Long>, QueryByExampleExecutor<Caly> {

//    long countAll();

//    Page<Caly> findByNameLikeIgnoreCase(String nameFilter, Pageable pageable);
//    long countByNameLikeIgnoreCase(String nameFilter);

}
