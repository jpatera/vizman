package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Caly;
import org.springframework.data.jpa.repository.JpaRepository;

//@Transactional(readOnly = true)
public interface CalyRepo extends JpaRepository<Caly, Long> {


}
