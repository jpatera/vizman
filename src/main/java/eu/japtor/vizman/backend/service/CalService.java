package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Calym;
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


    Page<Caly> fetchCalysByExameple(Example<Caly> example, Pageable pageable);
    long countCalysByExample(Example<Caly> example, Pageable pageable);

    List<Integer> fetchCalyYrList();

    List<Calym> fetchAllCalyms();
    long countAllCalyms();

    List<CalTreeNode> fetchCalymNodesByYear(Integer year);
    List<Calym> fetchCalymsByYear(Integer year);
    long countCalymsByYear(Integer yr);

    CalyHol fetchCalyHol(LocalDate holDate);
    boolean calyHolÈxist(LocalDate holDate);

    void generateAndSaveCalYearWorkFonds(Integer yr);
    Map<YearMonth, Integer> calcWorkDayCountsForYr(Integer yr);
}
