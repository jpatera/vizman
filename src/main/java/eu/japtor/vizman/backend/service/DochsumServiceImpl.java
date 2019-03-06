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
import java.util.List;


@Service
public class DochsumServiceImpl implements DochsumService, HasLogger {

    private DochsumRepo dochsumRepo;

    @Autowired
    public DochsumServiceImpl(DochRepo dochRepo) {
        super();
        this.dochsumRepo = dochsumRepo;
    }

    @Override
    public List<Dochsum> fetchDochsumForPersonAndDate(Long personId, LocalDate dochDate) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
        return dochsumRepo.findByPersonIdAndDochDate(personId, dochDate);
    }

    @Override
    public long countDochsumForPersonAndDate(Long personId, LocalDate dochDate) {
        return dochsumRepo.countByPersonIdAndDochDate(personId, dochDate);
    }
}
