package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.Dochsum;
import eu.japtor.vizman.backend.entity.DochsumZak;

import java.time.YearMonth;
import java.util.List;

public interface DochsumZakService {

    List<DochsumZak> fetchDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm);

    long countDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm);

//    boolean updateDochsumZaksForPersonAndMonth(
//            Long personId
//            , YearMonth ym
//            , List<DochsumZak> dsZaks
//    );

    boolean updateDochsumZaksForPersonAndMonth(
            Long pruhPersonId
            , YearMonth pruhYm
            , int pruhDayMax
            , List<PruhZak> pruhZaks
    );
}
