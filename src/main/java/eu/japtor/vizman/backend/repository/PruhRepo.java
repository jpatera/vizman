package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.Pruh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;

//@Transactional(readOnly = true)
public interface PruhRepo extends JpaRepository<Pruh, Long> {

    public Pruh findFirstByYmAndPersonId(YearMonth ym, Long personId);

    public Pruh findFirstByYmAndPersonIdAndState(YearMonth ym, Long personId, Integer state);

    public Pruh findFirstByPersonIdAndState(Long personId, Integer state);
}
