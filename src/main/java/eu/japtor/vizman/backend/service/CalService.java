package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Calym;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CalService {

    List<Caly> fetchAllCalys(CalTreeNode probe, Pageable pageable);
    long countAllCalys(CalTreeNode probe, Pageable pageable);

//    List<CalTreeNode> fetchAllCalRootNodes();

    List<Calym> fetchAllCalyms();
    List<CalTreeNode> fetchCalymNodesByYear(Integer year);
    List<Calym> fetchCalymsByYear(Integer year);

    CalyHol fetchCalyHol(LocalDate holDate);
    boolean calyHolÈxist(LocalDate holDate);

}
