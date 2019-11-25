package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Calym;

import java.time.LocalDate;
import java.util.List;

public interface CalService {

    List<Caly> fetchAllCalys();

    List<Calym> fetchAllCalyms();

    List<CalTreeNode> fetchCalymsByYear(Integer year);

    CalyHol fetchCalyHol(LocalDate holDate);
    boolean calyHolÈxist(LocalDate holDate);

}
