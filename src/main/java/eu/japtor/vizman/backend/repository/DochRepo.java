package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DochRepo extends JpaRepository<Doch, Long> {

//    Doch findTopByPersonIdAndDochDate(Long personId, LocalDate dochDate);
//    List<Doch> findByPersonIdAndDochDateOrderByPersonIdFromTimeDesc(Long personId, LocalDate dochDate);
//    List<Doch> findByPersonIdAndDochDate(Long personId, LocalDate dochDate);
    List<Doch> findByPersonIdAndDochDateOrderByFromTimeDesc(Long personId, LocalDate dochDate);
    long countByPersonIdAndDochDate(Long personId, LocalDate dochDate);

//    new Sort(Sort.Direction.DESC, "<colName>")

    // List<Doch> findDistinctDochDateByPersonIdAndDochDateOrderByDochDateDesc(Long personId, LocalDate dochDate);
    List<DochDateOnly> findDistinctTop2ByPersonIdAndDochDateOrderByDochDateDesc(Long personId, LocalDate dochDate);

    public interface DochDateOnly {
        LocalDate getDochDate();
    }


    @Query(value = "SELECT TOP 1 * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 AND CIN_AKCE_TYP = 'ZK' " +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    public Doch findLastZkDochForPersonAndDate(Long personId, LocalDate dochDate);
//    public List<Doch> findLastZkDochForPersonAndDate(Long personId, LocalDate dochDate);

    @Query(value = "SELECT TOP 1 CDOCH FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    public Integer findLastCdochForPersonAndDate(Long personId, LocalDate dochDate);

    @Query(value = "SELECT * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    public List<Doch> findDochForPersonAndDate(Long personId, LocalDate dochDate);


    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE < ?2 " +
            " ORDER BY DOCH_DATE DESC ",  nativeQuery = true)
    public LocalDate findPrevDochDate(Long personId, LocalDate dochDate);


    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE > ?2 " +
            " ORDER BY DOCH_DATE ASC ",  nativeQuery = true)
    public LocalDate findNextDochDate(Long personId, LocalDate dochDate);


    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 " +
            " ORDER BY DOCH_DATE DESC ",  nativeQuery = true)
    public LocalDate findLastDochDate(Long personId);

}
