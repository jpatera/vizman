package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.entity.PersonWage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;
import java.util.LinkedList;


//@Transactional(readOnly = true)
public interface PersonWageRepo extends JpaRepository<PersonWage, Long> {

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.PERSON_WAGE WHERE PERSON_ID = ?1 AND "
            + " (YM_FROM <= ?2) AND ((YM_TO IS NULL) OR (YM_TO >= ?2)) ORDER BY YM_FROM DESC"
            , nativeQuery = true)
    PersonWage findPersonWageForMonth(Long personId, YearMonth ym);

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.PERSON_WAGE WHERE PERSON_ID = ?1 ORDER BY YM_FROM DESC"
            , nativeQuery = true)
    PersonWage findPersonLastWage(Long personId);

    LinkedList<PersonWage> findByPersonIdOrderByYmFromDesc(Long personId);

    @Query(value = "SELECT COUNT(*) FROM VIZMAN.PERSON_WAGE WHERE PERSON_ID = ?1 AND " +
            " ( ((?2 >= YM_FROM) AND (YM_TO IS NOT NULL) AND (?2 <= YM_TO)) " +
            "     OR " +
            "   ((?3 >= YM_FROM) AND (YM_TO IS NOT NULL) AND (?3 <= YM_TO)) " +
            "     OR " +
            "   ((?2 <= YM_FROM) AND (?3 >= YM_FROM)) " +
            "     OR " +
            "   ((?2 <= YM_TO) AND (?3 >= YM_TO)) )"
            ,  nativeQuery = true)
    long getCoincidingWages(Long personId, YearMonth ymFrom, YearMonth ymTo);

//    PersonWage findTopByPersonIdAndYmFromLessThanEqualOrderByYmFromDesc(Long personId, YearMonth ym);

}
