package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Role;

import java.time.LocalDate;
import java.util.List;

public interface DochService {

    List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate);

    long countDochForPersonAndDate(Long personId, LocalDate dochDate);

    LocalDate findPrevDochDate(Long personId, LocalDate dochDate);

    LocalDate findNextDochDate(Long personId, LocalDate dochDate);

    LocalDate findLastDochDate(Long personId);

}
