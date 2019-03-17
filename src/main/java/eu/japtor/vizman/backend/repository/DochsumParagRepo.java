package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.DochsumParag;
import eu.japtor.vizman.backend.entity.DochsumZak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface DochsumParagRepo extends JpaRepository<DochsumParag, Long> {

    List<DochsumParag> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);
    long countByPersonIdAndDsYm(Long personId, YearMonth dsYm);

}
