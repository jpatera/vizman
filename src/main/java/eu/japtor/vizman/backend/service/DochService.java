package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Role;

import java.time.LocalDate;
import java.util.List;

public interface DochService {

    List<Doch> fetchDochsByPersonIdAndDate(Long personId, LocalDate dDate);

    long countDochsByPersonIdAndDate(Long personId, LocalDate dDate);
}
