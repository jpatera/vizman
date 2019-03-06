package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DochsumRepo extends JpaRepository<Doch, Long> {

    List<Dochsum> findByPersonIdAndDochDate(Long personId, LocalDate dochDate);
    long countByPersonIdAndDochDate(Long personId, LocalDate dochDate);

    Doch findTop1ByPersonIdAndDochDate(Long personId, LocalDate dochDate);

}
