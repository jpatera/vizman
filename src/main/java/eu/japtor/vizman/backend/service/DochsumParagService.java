package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.DochsumParag;
import eu.japtor.vizman.backend.entity.DochsumZak;

import java.time.YearMonth;
import java.util.List;

public interface DochsumParagService {

    List<DochsumParag> fetchDochsumParagsForPersonAndYm(Long personId, YearMonth dsYm);

    long countDochsumParagsForPersonAndYm(Long personId, YearMonth dsYm);


}
