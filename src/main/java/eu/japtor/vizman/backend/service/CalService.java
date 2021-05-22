package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface CalService {

    List<Caly> fetchAllCalys();
    long countAllCalys();


    Page<Caly> fetchCalysByExample(Example<Caly> example, Pageable pageable);
//    Page<CalyHolTreeNode> fetchCalHolNodesByExample(Example<Caly> example, Pageable pageable);
//    List<Caly> fetchCalysByExample(Example<Caly> example, Pageable pageable);
    long countCalysByExample(Example<Caly> example, Pageable pageable);

    List<Integer> fetchCalyYrList();
    boolean calyExist(Integer year);
    Caly fetchCaly(Integer year);

    List<Calym> fetchAllCalyms();
    long countAllCalyms();

    List<CalTreeNode> fetchCalymNodesByYear(Integer year);
    List<Calym> fetchCalymsByYear(Integer year);
    long countCalymsByYear(Integer yr);

    long countCalyHolsByExample(Example<CalyHol> example, Pageable pageable);
    long countCalyHolsByYear(Integer yr);

    CalyHol fetchCalyHols(LocalDate holDate);
//    List<CalyHol> fetchCalyHolsByExample(Integer yr, Pageable pageable);
    Page<CalyHol> fetchCalyHolsByExample(Example<CalyHol> example, Pageable pageable);
    boolean calyHolExist(LocalDate holDate);

    CalyHol saveCalyHol(CalyHol itemToSave);
    void deleteCalyHol(CalyHol itemToDelete);

    void generateAndSaveCalYearWorkFonds(Integer yr);
    Map<YearMonth, Integer> calcWorkDayCountsForYr(Integer yr);
    List<LocalDate> fetchCalyHolDateListByYear(Integer yr);
}
