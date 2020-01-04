package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.*;
import java.util.List;

public interface DochRepo extends JpaRepository<Doch, Long> {

//    Doch findTopByPersonIdAndDochDate(Long personId, LocalDate dochDate);
//    List<Doch> findByPersonIdAndDochDateOrderByPersonIdFromTimeDesc(Long personId, LocalDate dochDate);
//    List<Doch> findByPersonIdAndDochDate(Long personId, LocalDate dochDate);

    List<Doch> findByPersonIdAndDochDateOrderByFromTimeDesc(Long personId, LocalDate dochDate);
    long countByPersonIdAndDochDate(Long personId, LocalDate dochDate);

//    new Sort(Sort.Direction.DESC, "<colName>")

//    interface DochDateOnly {
//        LocalDate getDochDate();
//    }
//    List<DochDateOnly> findDistinctTop2ByPersonIdAndDochDateOrderByDochDateDesc(Long personId, LocalDate dochDate);

    Doch findTop1ByPersonIdAndDochDateAndDochState(Long personId, LocalDate dochDate, String dochState);

    Doch findTop1ByPersonIdAndDochDateAndCinCinKod(Long personId, LocalDate dochDate, Cin.CinKod cinKod);

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 AND CIN_AKCE_TYP = 'ZK' " +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    Doch findLastZkDochForPersonAndDate(Long personId, LocalDate dochDate);
//    public List<Doch> findLastZkDochForPersonAndDate(Long personId, LocalDate dochDate);

    @Query(value = "SELECT TOP 1 * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
            " AND CIN_CIN_KOD <> 'OA' AND CIN_CIN_KOD <> 'OA'" +
//            " AND CALCPRAC = TRUE AND CIN_CIN_KOD <> 'OA' AND CIN_CIN_KOD <> 'OA'" +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    Doch findLastPracDochForPersonAndDateNotOa(Long personId, LocalDate dochDate);

    @Query(value = "SELECT TOP 1 CDOCH FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
            " ORDER BY CDOCH DESC", nativeQuery = true)
    Integer findLastCdochForPersonAndDate(Long personId, LocalDate dochDate);

    @Query(value = "SELECT TOP 1 CDOCH FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
            " ORDER BY CDOCH", nativeQuery = true)
    Integer findFirstCdochForPersonAndDate(Long personId, LocalDate dochDate);

//    @Query(value = "SELECT * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE = ?2 " +
//            " ORDER BY CDOCH DESC", nativeQuery = true)
//    List<Doch> findDochForPersonAndDate(Long personId, LocalDate dochDate);
    List<Doch> findByPersonIdAndDochDateOrderByCdochDesc(Long personId, LocalDate dochDate);

    @Query(value = "SELECT * FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE >= ?2  AND DOCH_DATE <= ?3 " +
            " ORDER BY DOCH_DATE ASC, ID ASC ",  nativeQuery = true)
    List<Doch> findByPersonIdAndDochYmForReport(Long personId, LocalDate dochDate1, LocalDate dochDate2);

    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE < ?2 " +
            " ORDER BY DOCH_DATE DESC ",  nativeQuery = true)
    LocalDate findPrevDochDate(Long personId, LocalDate dochDate);


    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 AND DOCH_DATE > ?2 " +
            " ORDER BY DOCH_DATE ASC ",  nativeQuery = true)
    LocalDate findNextDochDate(Long personId, LocalDate dochDate);


    @Query(value = "SELECT TOP 1 DISTINCT DOCH_DATE FROM VIZMAN.DOCH WHERE PERSON_ID = ?1 " +
            " ORDER BY DOCH_DATE DESC ",  nativeQuery = true)
    LocalDate findLastDochDate(Long personId);


    void deleteByPersonIdAndDochDate(final Long personId, final LocalDate dochDate);

    @Query(value = "SELECT COUNT (DISTINCT doch_date) FROM vizman.doch WHERE "
            + "PERSON_ID = ?1 AND YEAR(DOCH_DATE) = ?2 AND MONTH(DOCH_DATE) = ?3 "
//            + " AND (CIN_CIN_KOD = 'P' OR CIN_CIN_KOD = 'PM' OR CIN_CIN_KOD = 'SC' OR CIN_CIN_KOD = 'L') "
            + " AND CALCPRAC = true "
            , nativeQuery = true)
    long countNonParagDays(Long personId, Integer year, Integer month);

    @Query(value = "SELECT SUM (doch_dur) FROM vizman.doch WHERE "
            + "PERSON_ID = ?1 AND YEAR(DOCH_DATE) = ?2 AND MONTH(DOCH_DATE) = ?3 "
//            + " AND (CIN_CIN_KOD = 'P' OR CIN_CIN_KOD = 'PM' OR CIN_CIN_KOD = 'SC' OR CIN_CIN_KOD = 'L') "
            + " AND CALCPRAC = true "
            , nativeQuery = true)
    Long sumNonParagDochDur(Long personId, Integer year, Integer month);

}
