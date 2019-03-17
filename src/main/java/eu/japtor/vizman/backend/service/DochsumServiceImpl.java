package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.Dochsum;
import eu.japtor.vizman.backend.repository.DochRepo;
import eu.japtor.vizman.backend.repository.DochsumRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
public class DochsumServiceImpl implements DochsumService, HasLogger {

    private DochsumRepo dochsumRepo;

    @Autowired
    public DochsumServiceImpl(DochsumRepo dochsumRepo) {
        super();
        this.dochsumRepo = dochsumRepo;
    }

    @Override
    public List<Dochsum> fetchDochsumForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        Integer dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
//        return dochsumRepo.findByPersonIdAndDsYm(personId, dsYmInt);
        return dochsumRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public long countDochsumForPersonAndDate(Long personId, YearMonth dsYm) {
        return dochsumRepo.countByPersonIdAndDsYm(personId, dsYm);
    }
}
