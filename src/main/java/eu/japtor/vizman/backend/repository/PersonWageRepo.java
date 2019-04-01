package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.entity.PersonWage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;


//@Transactional(readOnly = true)
public interface PersonWageRepo extends JpaRepository<PersonWage, Long> {

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.PERSON_WAGE WHERE person_id = ?1 AND YM_FROM <= ?2 ORDER BY YM_FROM DESC"
            , nativeQuery = true)
    PersonWage findPersonWageForMonth(Long personId, YearMonth ym);

//    @Query(value = "SELECT pw FROM PersonWage pw WHERE pw.personId = ?1 AND pw.ymFrom <= ?2 ORDER BY pw.ymFrom DESC")
//    PersonWage findPersonWageForMonth(Long personId, YearMonth ym);

//    PersonWage findTopByPersonIdAndYmFromLessThanEqualOrderByYmFromDesc(Long personId, YearMonth ym);

}
