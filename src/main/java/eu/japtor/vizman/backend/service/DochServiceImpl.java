package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<Doch> fetchDochsByPersonIdAndDate(Long personId, LocalDate dochDate) {
        return dochRepo.findByPersonIdAndDDateOrderByIdDesc(personId, dochDate);
    }

    @Override
    public long countDochsByPersonIdAndDate(Long personId, LocalDate dochDate) {
        return dochRepo.countByIdAndDDate(personId, dochDate);
    }
}
