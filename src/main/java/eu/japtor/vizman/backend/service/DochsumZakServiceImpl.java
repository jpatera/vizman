package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Dochsum;
import eu.japtor.vizman.backend.entity.DochsumZak;
import eu.japtor.vizman.backend.repository.DochRepo;
import eu.japtor.vizman.backend.repository.DochsumRepo;
import eu.japtor.vizman.backend.repository.DochsumZakRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;


@Service
public class DochsumZakServiceImpl implements DochsumZakService, HasLogger {

    @Autowired
    private DochsumZakRepo dochsumZakRepo;

//    @Autowired
//    public DochsumZakServiceImpl(DochsumZakRepo dochsumZakRepo) {
//        super();
//        this.dochsumZakRepo = dochsumZakRepo;
//    }

    @Override
    public List<DochsumZak> fetchDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public long countDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.countByPersonIdAndDsYm(personId, dsYm);
    }
}
