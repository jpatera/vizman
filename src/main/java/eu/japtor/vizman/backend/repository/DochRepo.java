package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DochRepo extends JpaRepository<Doch, Long> {

    Doch findTopByPersonIdAndDDate(Long personId, LocalDate dDate);

    List<Doch> findByPersonIdAndDDateOrderByIdDesc(Long personId, LocalDate dDate);

//    new Sort(Sort.Direction.DESC, "<colName>")

    long countByIdAndDDate(Long id, LocalDate dDate);

}
