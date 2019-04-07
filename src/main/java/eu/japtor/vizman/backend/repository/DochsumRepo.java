package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DochsumRepo extends JpaRepository<Dochsum, Long> {

//    List<Dochsum> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);
    List<Dochsum> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);

    long countByPersonIdAndDsYm(Long personId, YearMonth dsYm);

//    Doch findTop1ByPersonIdAndDochDate(Long personId, YearMonth dsYm);

    void deleteByDsDateAndPersonId(LocalDate dochDate, Long personId);
}
