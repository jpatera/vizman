package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
public class DochServiceImpl implements DochService {

    private DochRepo dochRepo;

    @Autowired
    public DochServiceImpl(DochRepo dochRepo) {
        super();
        this.dochRepo = dochRepo;
    }

    @Override
    public List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
        return dochRepo.findDochForPersonAndDate(personId, dochDate);
    }

    @Override
    public long countDochForPersonAndDate(Long personId, LocalDate dochDate) {
        return dochRepo.countByPersonIdAndDochDate(personId, dochDate);
    }


    @Override
    public LocalDate findPrevDochDate(Long personId, LocalDate dochDate) {
//        List<Doch> dochList = dochRepo.findDistinctTop2ByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        List<DochRepo.DochDateOnly> dochDateList = dochRepo.findDistinctTop2ByPersonIdAndDochDateOrderByDochDateDesc(personId, dochDate);
//        LocalDate prevDochDateList = dochRepo.findPrevDochDate(personId, dochDate);
//        if (dochDateList.size() > 0) {
//            return dochDateList.get(0).getDochDate();
//        }
//        return null;

        return dochRepo.findPrevDochDate(personId, dochDate);
    }

    @Override
    public LocalDate findNextDochDate(Long personId, LocalDate dochDate) {
        return dochRepo.findNextDochDate(personId, dochDate);
    }

    @Override
    public LocalDate findLastDochDate(Long personId) {
        return dochRepo.findLastDochDate(personId);
    }

    @Override
    public Doch addFirstPrichod(Doch doch) {
        return dochRepo.save(doch);
    }

}
