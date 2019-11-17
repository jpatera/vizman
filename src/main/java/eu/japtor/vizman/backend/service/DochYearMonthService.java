package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.DochMonth;
import eu.japtor.vizman.backend.entity.DochYear;

import java.time.YearMonth;
import java.util.List;

public interface DochYearMonthService {

    List<DochMonth> fetchRepDochMonthForPersonAndYm(Long personId, YearMonth dochYm);

    List<DochYear> fetchRepDochYearForPersonAndYear(Long personId, Integer dochYear);

}
