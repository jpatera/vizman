package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Calym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface CalymRepo extends JpaRepository<Calym, Long> {

    @Query(value = "SELECT * FROM vizman.calym WHERE ym / 100 = ?1 ", nativeQuery = true)
    List<Calym> findByYr(Integer yr);

}
