package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.CalyHol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Transactional(readOnly = true)
public interface CalyHolRepo extends JpaRepository<CalyHol, Long> {

    CalyHol findByHolDate(LocalDate holDate);

}
