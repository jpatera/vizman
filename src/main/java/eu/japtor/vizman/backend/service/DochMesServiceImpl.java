package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.entity.DochMes;
import eu.japtor.vizman.backend.repository.CalymRepo;
import eu.japtor.vizman.backend.repository.DochMesRepo;
import eu.japtor.vizman.backend.repository.DochRepo;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DochMesServiceImpl implements DochMesService, HasLogger {

    private List<Calym> calymList;

    private DochMesRepo dochMesRepo;

    @Autowired
    CalymRepo calymRepo;

    @Autowired
    public DochMesServiceImpl(DochMesRepo dochMesRepo) {
        super();
        this.dochMesRepo = dochMesRepo;
    }

    @PostConstruct
    public void postInit() {
        calymList = calymRepo.findAll(Sort.by(Sort.Direction.DESC, Calym.SORT_PROP_YM));
    }


    @Override
    public List<DochMes> fetchRepDochMesForPersonAndYm(Long personId, YearMonth dochYm) {
        LocalDate dateStart = dochYm.atDay(1);
        LocalDate dateEnd = dochYm.atEndOfMonth();
        BigDecimal monthHourFond = getFondFromCalymList(dochYm);
        String monthHourFondStr = null == monthHourFond ? "" : VzmFormatUtils.decHodFormat.format(monthHourFond);
        List<DochMes> dochMesRecs = dochMesRepo.findByPersonIdAndDochYm(personId, dateStart, dateEnd);
        for(DochMes dm : dochMesRecs) {
            dm.setMonthHourFond(monthHourFondStr);
        }
        return dochMesRecs;
    }

    private  BigDecimal getFondFromCalymList(final YearMonth ym) {
        if (null == calymList) {
            return null;
        } else {
            return calymList.stream()
                    .filter(calym -> calym.getYm().equals(ym))
                    .map(calym -> calym.getMonthFondHours())
                    .findFirst().orElse(null);
        }
    }

}
