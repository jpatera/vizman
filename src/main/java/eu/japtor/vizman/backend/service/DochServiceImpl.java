package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class DochServiceImpl implements DochService {

    private DochRepo dochRepo;

    @Autowired
    public DochServiceImpl(DochRepo dochRepo) {
        super();
        this.dochRepo = dochRepo;
    }

    @Override
    public Doch fetchDochsByPersonAndDate(Long personId, LocalDate dDate) {
        return null;
    }

    @Override
    public long countDochsByPersonAndDate(Long personId, LocalDate dDate) {
        return 0;
    }
}
