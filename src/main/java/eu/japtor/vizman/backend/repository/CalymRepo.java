package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Calym;
import org.springframework.data.jpa.repository.JpaRepository;


//@Transactional(readOnly = true)
public interface CalymRepo extends JpaRepository<Calym, Long> {


}