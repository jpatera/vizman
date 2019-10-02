package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface ZaknRepo extends JpaRepository<Zakn, Long> {

    List<Zakn> findByZakIdOrderByPersonIdAscDatePruhDesc(Long zakId);

//    @Query(value = "SELECT zak_id, ds_ym, SUM(dsz_work_pruh) AS work_pruh_ym, SUM(dsz_mzda) as nakl_mzda_ym, sazba " +
//    " FROM vizman.dochsum_zak " +
//    " WHERE zak_id = 1000576 " +
//    " AND dsz_mzda IS NOT null " +
//    " GROUP BY zak_id , ds_ym, sazba " +
//    " ORDER BY DS_YM DESC "
//    , nativeQuery = true)
    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id = ?1"
        , nativeQuery = true)
    List<Zakn> findByZakIdSumByYm(Long zakId);
}
