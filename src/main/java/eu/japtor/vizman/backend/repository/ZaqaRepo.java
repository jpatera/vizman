package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.entity.Zaqa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;
import java.util.LinkedList;
import java.util.List;


//@Transactional(readOnly = true)
public interface ZaqaRepo extends JpaRepository<Zaqa, Long> {

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.ZAQA WHERE ID_ZAK = ?1 ORDER BY ROK DESC, QA DESC"
            , nativeQuery = true)
    PersonWage findZakLastQa(Long idZak);

    List<Zaqa> findByZakrIdOrderByRokDesc(Long idZak);
}
