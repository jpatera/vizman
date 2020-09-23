package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.entity.DochMonth;
import eu.japtor.vizman.backend.entity.DochYear;
import eu.japtor.vizman.backend.repository.CalyRepo;
import eu.japtor.vizman.backend.repository.CalymRepo;
import eu.japtor.vizman.backend.repository.DochMonthRepo;
import eu.japtor.vizman.backend.repository.DochYearRepo;
import eu.japtor.vizman.backend.utils.VzmFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
public class DochYearMonthServiceImpl implements DochYearMonthService, HasLogger {

    private List<Calym> calymList;
    private List<Caly> calyList;

    private DochMonthRepo dochMonthRepo;
    private DochYearRepo dochYearRepo;

    @Autowired
    CalymRepo calymRepo;

    @Autowired
    CalyRepo calyRepo;

    @Autowired
    public DochYearMonthServiceImpl(DochMonthRepo dochMonthRepo, DochYearRepo dochYearRepo) {
        super();
        this.dochMonthRepo = dochMonthRepo;
        this.dochYearRepo = dochYearRepo;
    }

    @PostConstruct
    public void postInit() {
        calymList = calymRepo.findAll(Sort.by(Sort.Direction.DESC, Calym.SORT_PROP_YM));
        calyList = calyRepo.findAll(Sort.by(Sort.Direction.DESC, Caly.SORT_PROP_YR));
    }


    @Override
    public List<DochMonth> fetchRepDochMonthForPersonAndYm(Long personId, YearMonth dochYm) {
        LocalDate dateStart = dochYm.atDay(1);
        LocalDate dateEnd = dochYm.atEndOfMonth();
        BigDecimal monthHourFond = getMonthFondHours(dochYm);
        String monthHourFondStr = null == monthHourFond ? "" : VzmFormatUtils.DEC_HOD_FORMAT.format(monthHourFond);
        List<DochMonth> dochMonthRecs = dochMonthRepo.findByPersonIdAndDochYm(personId, dateStart, dateEnd);
        for(DochMonth dm : dochMonthRecs) {
            dm.setMonthFondHours(monthHourFondStr);
        }
        return dochMonthRecs;
    }


    @Override
    public List<DochYear> fetchRepDochYearForPersonAndYear(Long personId, Integer dochYear) {
        YearMonth ymStart = YearMonth.of(dochYear, 1);
        YearMonth ymEnd = YearMonth.of(dochYear, 12);

        BigDecimal yearFondHours = getYearFondHours(dochYear);
        String yearFondHoursStr = null == yearFondHours ? "" : VzmFormatUtils.DEC_HOD_FORMAT.format(yearFondHours);

        BigDecimal yearFondDays = getYearFondDays(dochYear);
        String yearFondDaysStr = null == yearFondDays ? "" : VzmFormatUtils.DEC_HOD_FORMAT.format(yearFondDays);

        List<DochYear> dochYearRecs = dochYearRepo.findByPersonIdAndDochYm(personId, ymStart, ymEnd);
        for(DochYear dy : dochYearRecs) {
            dy.setYearFondHours(yearFondHoursStr);
            dy.setYearFondDays(yearFondDaysStr);
        }

        return dochYearRecs;
    }

    // TODO: predelat do cache v calymRepo (viz tez PruhView)
    private  BigDecimal getMonthFondHours(final YearMonth ym) {
        if (null == calymList) {
            return null;
        } else {
            return calymList.stream()
                    .filter(calym -> calym.getYm().equals(ym))
                    .map(calym -> calym.getMonthFondHours())
                    .findFirst().orElse(null);
        }
    }

    private  BigDecimal getYearFondHours(final Integer year) {
        if (null == calyList) {
            return null;
        } else {
            return calyList.stream()
                    .filter(caly -> caly.getYr().equals(year))
                    .map(caly -> caly.getYearFondHours())
                    .findFirst().orElse(null);
        }
    }

    private  BigDecimal getYearFondDays(final Integer year) {
        if (null == calyList) {
            return null;
        } else {
            return calyList.stream()
                    .filter(caly -> caly.getYr().equals(year))
                    .map(caly -> caly.getYearFondDays())
                    .findFirst().orElse(null);
        }
    }

}
