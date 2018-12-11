package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DochRepo extends JpaRepository<Doch, Long> {

    Doch findTopByPersonIdAndDDate(Long personId, LocalDate dDate);

}
