package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.DochMes;
import eu.japtor.vizman.backend.repository.DochMesRepo;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
public class DochMesServiceImpl implements DochMesService, HasLogger {

    private DochMesRepo dochMesRepo;

    @Autowired
    public DochMesServiceImpl(DochMesRepo dochMesRepo) {
        super();
        this.dochMesRepo = dochMesRepo;
    }

    @Override
    public List<DochMes> fetchRepDochMesForPersonAndYm(Long personId, YearMonth dochYm) {
        LocalDate dateStart = dochYm.atDay(1);
        LocalDate dateEnd = dochYm.atEndOfMonth();
        return dochMesRepo.findByPersonIdAndDochYm(personId, dateStart, dateEnd);
    }
}
