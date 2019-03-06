package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;

import java.time.LocalDate;
import java.util.List;

public interface DochsumService {

    List<Dochsum> fetchDochsumForPersonAndDate(Long personId, LocalDate dochDate);

    long countDochsumForPersonAndDate(Long personId, LocalDate dochDate);


}
