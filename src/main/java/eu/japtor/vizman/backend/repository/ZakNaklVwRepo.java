package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZaknNaklVw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface ZakNaklVwRepo extends JpaRepository<ZaknNaklVw, Long> {

    List<ZaknNaklVw> findByZakIdOrderByPersonIdAscDatePruhDesc(Long zakId);

}
