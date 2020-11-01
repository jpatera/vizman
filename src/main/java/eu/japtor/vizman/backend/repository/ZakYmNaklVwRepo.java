package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakYmNaklVw;
import eu.japtor.vizman.backend.entity.ZaknNaklVw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface ZakYmNaklVwRepo extends JpaRepository<ZakYmNaklVw, Long>, ZakYmNaklVwRepoCustom {

//    @Query(value = "SELECT zak_id, ds_ym, SUM(dsz_work_pruh) AS work_pruh_ym, SUM(dsz_mzda) as nakl_mzda_ym, sazba " +
//    " FROM vizman.dochsum_zak " +
//    " WHERE zak_id = 1000576 " +
//    " AND dsz_mzda IS NOT null " +
//    " GROUP BY zak_id , ds_ym, sazba " +
//    " ORDER BY DS_YM DESC "
//    , nativeQuery = true)
    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id = ?1"
        , nativeQuery = true)
    List<ZakYmNaklVw> findByZakIdSumByYm(Long zakId);

// INFO: Following query returns  for more than one zak_id a correct number of records, but  all
//       contein only one of the submitted zak_id values. Probbably som H2 & Hibernate bug.
//  SEE: Alternative  method in  eu.japtor.vizman.backend.repository.ZaknRepoCustomImpl.findByZakIdsSumByYm
//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (:ids) ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//        , nativeQuery = true)
//    List<ZaknNaklVw> findByZakIdsSumByYm(@Param("ids") List<Long> zakIds);

//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id=4026618 or zak_id=4026617 or zak_id=4026616 ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//            , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds);

//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id = 4026618 or zak_id = 4026616 ORDER BY ZAK_ID DESC, PERSON_ID ASC, YM_PRUH DESC"
//            , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(@Param("ids") List<Long> zakIds);



    // Last attempt:
//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in  (:ids) ORDER BY ZAK_ID DESC, PERSON_ID ASC, YM_PRUH DESC"
//            , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(@Param("ids") List<Long> zakIds);


}
