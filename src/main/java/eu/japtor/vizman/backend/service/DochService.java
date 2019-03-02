package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface DochService {

    List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate);

    long countDochForPersonAndDate(Long personId, LocalDate dochDate);

    LocalDate findPrevDochDate(Long personId, LocalDate dochDate);

    LocalDate findNextDochDate(Long personId, LocalDate dochDate);

    LocalDate findLastDochDate(Long personId);

//    Doch addFirstPrichod(Doch doch);

//    Doch addPrichod(Doch doch);

//    Doch closePrevZkDochAndOpenNew(Doch newDoch);

//    Doch stampOdchodDefinitive(Doch dochInsideRec);

//    Doch stampOdchodAndNewOutsideRec(Doch insideDochRec, Cin.CinKod cinKodOut, LocalTime fromTimeOut);

    Doch openFirstRec(Doch firstDochRec);

    Doch closeLastRec(Doch lastDochRec);

    Doch closeLastRecAndOpenNew(Doch lastDochRec, Doch newDochRec);

    void removeLastZkDochAndReopenPrev(Doch doch);

    boolean removeAllDochRecsForPersonAndDate(Long personId, LocalDate dochDate);

}
