package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.CalyHol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


//@Transactional(readOnly = true)
public interface CalyHolRepo extends JpaRepository<CalyHol, Long>, QueryByExampleExecutor<CalyHol> {

    CalyHol findByHolDate(LocalDate holDate);

    @Query(value = "SELECT count(*) FROM vizman.caly_hol WHERE yr = ?1 "
            , nativeQuery = true)
    long countByYr(Integer yr);

    List<CalyHol> findByYr(Integer yr);
}
