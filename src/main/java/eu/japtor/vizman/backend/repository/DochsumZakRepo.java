package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.DochsumZak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.YearMonth;
import java.util.List;

public interface DochsumZakRepo extends JpaRepository<DochsumZak, Long> {

    List<DochsumZak> findByPersonIdAndDsYm(Long personId, YearMonth dsYm);
    long countByPersonIdAndDsYm(Long personId, YearMonth dsYm);

//    @Query(value = "DELETE FROM VIZMAN.DOCHSUM_ZAK WHERE person_id = ?1 AND DS_YM = ?2"
//            , nativeQuery = true)
    void deleteAllByPersonIdAndDsYm(Long personId, YearMonth ym);

    @Modifying
    @Query("delete from DochsumZak dsZaks where dsZaks.id in ?1")
    void deleteDochsumZaksWithIds(List<Long> ids);

    @Modifying
    @Query("delete from DochsumZak dsz where dsz.dsYm = ?1 and dsz.personId = ?2 and dsz.zakId in ?3")
    void deleteDochsumZaksByPruhAndZakIds(YearMonth pruhYm, Long pruhPersonId, List<Long> zakIds);

    @Modifying
    @Query("DELETE FROM DochsumZak dsz WHERE dsz.dsYm = ?1 AND dsz.personId = ?2 AND dsz.dszWorkPruh IS NULL OR dsz.dszWorkPruh = 0")
    void deleteZeroDochsumZaksByPruhAndZakIds(YearMonth pruhYm, Long pruhPersonId);

}
