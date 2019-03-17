package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.DochsumParag;
import eu.japtor.vizman.backend.entity.DochsumZak;
import eu.japtor.vizman.backend.repository.DochsumParagRepo;
import eu.japtor.vizman.backend.repository.DochsumZakRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;


@Service
public class DochsumParagServiceImpl implements DochsumParagService, HasLogger {

    @Autowired
    private DochsumParagRepo dochsumParagRepo;

    @Override
    public List<DochsumParag> fetchDochsumParagsForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumParagRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public long countDochsumParagsForPersonAndYm(Long personId, YearMonth dsYm) {
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumParagRepo.countByPersonIdAndDsYm(personId, dsYm);
    }
}
