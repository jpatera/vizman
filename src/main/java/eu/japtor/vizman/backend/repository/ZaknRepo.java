package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ZaknRepo extends JpaRepository<Zakn, Long> {

    List<Zakn> findByZakIdOrderByPersonIdAscDatePruhDesc(Long zakId);

}
