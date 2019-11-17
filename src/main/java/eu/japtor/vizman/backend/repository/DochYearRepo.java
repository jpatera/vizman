package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.DochYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface DochYearRepo extends JpaRepository<DochYear, Long> {

    @Query("SELECT dr AS DDRR FROM DochYear dr WHERE "
            + " (dr.personId = :personId) "
            + " AND (dr.dochYm >= :ymStart) "
            + " AND (dr.dochYm <= :ymEnd) ")
    List<DochYear> findByPersonIdAndDochYm(
            @Param("personId") Long personId, @Param("ymStart") YearMonth ymStart, @Param("ymEnd") YearMonth ymEnd
    );
}
