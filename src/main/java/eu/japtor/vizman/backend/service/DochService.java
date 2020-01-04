package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DochService {

    List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate);

    long countDochForPersonAndDate(Long personId, LocalDate dochDate);

    LocalDate fetchPrevDochDate(Long personId, LocalDate dochDate);

    LocalDate findNextDochDate(Long personId, LocalDate dochDate);

    LocalDate findLastDochDate(Long personId);

    Doch openFirstRec(Doch firstDochRec);

    Doch addSingleRec(Doch singleDochRec);

    Doch closeLastRec(Doch lastDochRec);

    Doch closeRecAndOpenNew(Doch lastDochRec, Doch newDochRec);

    void removeLastZkDochAndReopenPrev(Doch doch);

    void removeDochRec(Long personId, LocalDate dochDate, Cin.CinKod cinKod);

    boolean removeAllDochRecsForPersonAndDate(Long personId, LocalDate dochDate);

    BigDecimal calcKoefP8(Long personId, YearMonth ym);

}
