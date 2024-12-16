package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZaknNaklVw;
import eu.japtor.vizman.backend.entity.Zakr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface ZakNaklVwRepo extends JpaRepository<ZaknNaklVw, Long> {

    List<ZaknNaklVw> findByZakIdOrderByPersonIdAscDatePruhDesc(Long zakId);

    @Query(value = "SELECT * FROM vizman.zak_nakl_view znv WHERE (znv.PERSON_ID = :personId) ORDER BY CKONT DESC, CZAK DESC, DATE_PRUH DESC",
            nativeQuery = true)
    List<ZaknNaklVw> findPersonReportOfWorkItems(@Param("personId") Long personId);

}
