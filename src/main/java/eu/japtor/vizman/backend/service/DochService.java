package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Role;

import java.time.LocalDate;
import java.util.List;

public interface DochService {

    Doch fetchDochsByPersonAndDate(Long personId, LocalDate dDate);

    long countDochsByPersonAndDate(Long personId, LocalDate dDate);
}
