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
    @Query(value = "DELETE FROM VIZMAN.DOCHSUM_ZAK WHERE DS_YM = ?1 AND PERSON_ID = ?2 AND (DSZ_WORK_PRUH IS NULL OR DSZ_WORK_PRUH = 0) AND DAY(ds_Date) <> 1"
            , nativeQuery = true)
    void deleteZeroDochsumZaksByPruhYmAndPerson(YearMonth pruhYm, Long pruhPersonId);

//    @Query(value = "SELECT DISTINCT dsz.dsYm FROM DochsumZak dsz WHERE dsz.personId = ?1 AND dsz.dsYm <> ?2 ORDER BY dsz.dsYm DESC Limit 0, 25")
////            , nativeQuery = true)
//    @Query(value = "SELECT DISTINCT ds_ym FROM vizman.dochsum_zak WHERE person_id = ?1 AND ds_ym <> ?2 ORDER BY ds_ym DESC LIMIT 1"
//            , nativeQuery = true)
//    YearMonth getLastTwoPruhYmDochForPerson(Long pruhPersonId, YearMonth excludeYm);

//    YearMonth getTop1DsYmByPersonIdAndDsYmNotOrderByDsYmDesc(Long pruhPersonId, YearMonth excludeYm);

//    @Query(value = "SELECT TOP 1 DISTINCT * FROM VIZMAN.DOCHSUM_ZAK WHERE PERSON_ID = ?1 AND DS_YM <> ?2 ORDER BY DS_YM DESC"
//            , nativeQuery = true)
    @Query(value = "SELECT TOP 2 DISTINCT DS_YM FROM VIZMAN.DOCHSUM_ZAK WHERE PERSON_ID = ?1 ORDER BY DS_YM DESC"
            , nativeQuery = true)
    List<Integer> getLastTwoPruhYmDochForPerson(Long pruhPersonId);
//    DochsumZak getLastTwoPruhYmDochForPerson(Long pruhPersonId, YearMonth excludeYm);


}
