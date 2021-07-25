package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.DochMonthVw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface DochMonthRepo extends JpaRepository<DochMonthVw, Long> {

//    @Query(value = "SELECT * FROM vizman.DOCH_MES_VIEW WHERE person_id = ?1 AND doch_date >= ?2 AND doch_date <= ?3 "
//        , nativeQuery = true)
//    List<DochMonth> findByPersonIdAndDochYm(Long personId, LocalDate dateStart, LocalDate dateEnd);

//    @Query(value = "SELECT * FROM vizman.DOCH_MES_VIEW WHERE person_id = ?1 "
//        , nativeQuery = true)
//    List<DochMonth> findByPersonIdAndDochYm(Long personId, LocalDate dateStart, LocalDate dateEnd);

    @Query("SELECT dm AS DDMM FROM DochMonthVw dm WHERE "
            + " (dm.personId = :personId) "
            + " AND (dm.dochDate >= :dateStart) "
            + " AND (dm.dochDate <= :dateEnd) ")
    List<DochMonthVw> findByPersonIdAndDochYm(
            @Param("personId") Long personId, @Param("dateStart") LocalDate dateStart, @Param("dateEnd") LocalDate dateEnd
    );


    @Query("SELECT dm AS DDMM FROM DochMonthVw dm WHERE "
            + " (dm.personId IN :personIds) "
            + " AND (dm.dochDate >= :dateStart) "
            + " AND (dm.dochDate <= :dateEnd) "
    )
    LinkedList<DochMonthVw> findDochMonthByFilter(
            @Param("personIds") LinkedList<Long> personIds
            , @Param("dateStart") LocalDate dateStart
            , @Param("dateEnd") LocalDate dateEnd
    );
}
