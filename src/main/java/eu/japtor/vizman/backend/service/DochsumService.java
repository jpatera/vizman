package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DochsumService {

    List<Dochsum> fetchDochsumForPersonAndYm(Long personId, YearMonth dochYm);

    long countDochsumForPersonAndDate(Long personId, YearMonth dochYm);


}
