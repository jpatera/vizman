package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.CalyHol;
import org.springframework.data.jpa.repository.JpaRepository;


//@Transactional(readOnly = true)
public interface CalyHolRepo extends JpaRepository<CalyHol, Long> {


}
