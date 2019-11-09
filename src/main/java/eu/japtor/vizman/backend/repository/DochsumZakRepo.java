package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.DochsumZak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DochsumZakRepo extends JpaRepository<DochsumZak, Long> {

    DochsumZak findTop1ByPersonIdAndDsDateAndZakId(Long personId, LocalDate dsDate, Long zakId);

    List<DochsumZak> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);
    long countByPersonIdAndDsYm(Long personId, YearMonth dsYm);

    List<DochsumZak> findTop10ByZakIdOrderByDsDateDesc(Long zakId);

    Long countByZakId(Long zakId);

//    @Query(value = "DELETE FROM VIZMAN.DOCHSUM_ZAK WHERE person_id = ?1 AND DS_YM = ?2"
//            , nativeQuery = true)
    void deleteAllByPersonIdAndDsYm(Long personId, YearMonth ym);

    @Modifying
    @Query("DELETE FROM DochsumZak dsz where dsz.id in ?1")
    void deleteDochsumZaksWithIds(List<Long> ids);

    @Modifying
    @Query("DELETE from DochsumZak dsz where dsz.dsYm = ?1 and dsz.personId = ?2 and dsz.zakId in ?3")
    void deleteDochsumZaksByPruhAndZakIds(YearMonth pruhYm, Long pruhPersonId, List<Long> zakIds);


    @Modifying
//    @Query("DELETE FROM DochsumZak dsz WHERE dsz.dsYm = ?1 AND dsz.personId = ?2 AND (day(dsz.dsDate) <> 1) AND (dsz.dszWorkPruh IS NULL OR dsz.dszWorkPruh = 0)")
//    @Query(value = "DELETE FROM vizman.dochsum_zak WHERE ds_Ym = ?1 AND person_Id = ?2 AND DAY(ds_Date) <> 1 AND (dsz_Work_Pruh IS NULL OR dsz_Work_Pruh = 0)"
//            , nativeQuery = true)
//    @Query(value = "DELETE FROM VIZMAN.DOCHSUM_ZAK WHERE DS_YM = ?1 AND PERSON_ID = ?2 AND (DSZ_WORK_PRUH IS NULL OR DSZ_WORK_PRUH = 0) AND DAY(ds_Date) <> 1"
    @Query(value = "DELETE FROM VIZMAN.DOCHSUM_ZAK WHERE DS_YM = ?1 AND PERSON_ID = ?2 AND (DS_DATE > '1900-01-01') AND (DSZ_WORK_PRUH IS NULL OR DSZ_WORK_PRUH = 0)"
            , nativeQuery = true)
    void deleteZeroDochsumZaksByPruhYmAndPerson(YearMonth pruhYm, Long pruhPersonId);


//    @Query(value = "SELECT DISTINCT dsz.dsYm FROM DochsumZak dsz WHERE dsz.personId = ?1 AND dsz.dsYm <> ?2 ORDER BY dsz.dsYm DESC Limit 0, 25")
////            , nativeQuery = true)
//    @Query(value = "SELECT DISTINCT ds_ym FROM vizman.dochsum_zak WHERE person_id = ?1 AND ds_ym <> ?2 ORDER BY ds_ym DESC LIMIT 1"
//            , nativeQuery = true)
//    YearMonth getLastTwoPruhYmDochForPerson(Long pruhPersonId, YearMonth excludeYm);


//    @Query(value = "SELECT TOP 1 DISTINCT * FROM VIZMAN.DOCHSUM_ZAK WHERE PERSON_ID = ?1 AND DS_YM <> ?2 ORDER BY DS_YM DESC"
//            , nativeQuery = true)
    @Query(value = "SELECT TOP 2 DISTINCT DS_YM FROM VIZMAN.DOCHSUM_ZAK WHERE PERSON_ID = ?1 ORDER BY DS_YM DESC"
            , nativeQuery = true)
    List<Integer> getLastTwoPruhYmDochForPerson(Long pruhPersonId);
//    DochsumZak getLastTwoPruhYmDochForPerson(Long pruhPersonId, YearMonth excludeYm);


    @Modifying
    @Query(value = "UPDATE vizman.dochsum_zak dsz SET dsz.sazba = ( "
                + " SELECT pw2.wage FROM ( "
                    + " SELECT pw.wage, dsz2.id "
                    + " FROM vizman.DOCHSUM_ZAK AS dsz2 "
                    + " LEFT JOIN vizman.PERSON_WAGE AS pw "
                    + " ON (dsz2.person_id = pw.person_id) "
                    + " AND (dsz2.DS_YM BETWEEN pw.ym_from AND (CASE WHEN pw.ym_to IS NULL THEN 999999 ELSE pw.ym_to END)) "
                    + " WHERE pw.person_id = ?1 "
                    + " AND dsz2.DS_DATE > '2000-01-01' "
                    + " ) AS pw2 "
                + " WHERE pw2.id = dsz.id "
            + " ) "
            + " WHERE dsz.person_id = ?1 "
            + " AND dsz.DS_DATE > '2000-01-01' "
    , nativeQuery = true)
    void adjustSazbaByPerson(Long pruhPersonId);

    @Modifying
    @Query(value = "UPDATE vizman.dochsum_zak dsz "
            + " SET dsz.DSZ_MZDA = dsz.SAZBA * DSZ_WORK_PRUH "
            + " WHERE dsz.SAZBA IS NOT NULL "
            + " AND dsz.DSZ_WORK_PRUH IS NOT  NULL "
            + " AND person_id = ?1 "
            + " AND dsz.DS_DATE > '2000-01-01' "
    , nativeQuery = true)
    void adjustMzdaByPerson(Long pruhPersonId);

}
