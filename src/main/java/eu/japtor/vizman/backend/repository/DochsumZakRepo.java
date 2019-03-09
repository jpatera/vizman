package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;
import eu.japtor.vizman.backend.entity.DochsumZak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface DochsumZakRepo extends JpaRepository<DochsumZak, Long> {

    List<DochsumZak> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);
    long countByPersonIdAndDsYm(Long personId, YearMonth dsYm);

}
