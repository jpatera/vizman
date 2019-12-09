package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.CalyHolRepo;
import eu.japtor.vizman.backend.repository.CalyRepo;
import eu.japtor.vizman.backend.repository.CalymRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;


@Component
public class CalServiceImpl implements CalService {

    @Autowired
    CalyRepo calyRepo;

    @Autowired
    CalymRepo calymRepo;

    @Autowired
    CalyHolRepo calyHolRepo;


    // Calendar - years
    // ----------------

    @Override
    public List<Caly> fetchAllCalys() {
        return calyRepo.findAll();
    }

    @Override
    public long countAllCalys() {
        return calyRepo.findAll().size();
    }

    @Override
    public List<Integer> fetchCalyYrList() {
        return calyRepo.findAllYrList();
    }

//    @Override
//    public Page<Caly> fetchCalysByExample(Example<Caly> example, Pageable pageable) {
//        return calyRepo.findAll(example, pageable);
//    }

    @Override
    public Page<Caly> fetchCalysByExample(Example<Caly> example, Pageable pageable) {
        return calyRepo.findAll(example, pageable);
    }

//    @Override
//    public Page<CalHolTreeNode> fetchCalHolNodesByExample(Example<Caly> example, Pageable pageable) {
//        return calyRepo.findAll(example, pageable);
//    }


//    @Override
//    public long countCalysByExample(Example<Caly> example, Pageable pageable) {
//        return calyRepo.findAll(example, pageable).getTotalElements();
//    }

    @Override
    public long countCalysByExample(Example<Caly> example, Pageable pageable) {
//        return calyRepo.findAll(example, pageable).getSize();
        return calyRepo.findAll(example, pageable).getTotalElements();
    }


    // Calendar - YMs
    // --------------
    @Override
    public List<Calym> fetchAllCalyms() {
        return calymRepo.findAll();
    }

    @Override
    public long countAllCalyms() {
        return calymRepo.count();
    }

    @Override
    public List<CalTreeNode> fetchCalymNodesByYear(Integer yr) {
        // TODO try to avoid casting
        List<? extends Object> list = calymRepo.findByYr(yr);
        return (List<CalTreeNode>) list;
    }

    @Override
    public List<Calym> fetchCalymsByYear(Integer yr) {
        List<Calym> list = calymRepo.findByYr(yr);
        return list;
    }

    @Override
    public long countCalymsByYear(Integer yr) {
        return calymRepo.countByYr(yr);
    }

    @Transactional
    public void generateAndSaveCalYearWorkFonds(final Integer yr)
    {
        Map<YearMonth, Integer> calYearFonds = calcWorkDayCountsForYr(yr);

        for (YearMonth ym : calYearFonds.keySet()) {
            calymRepo.deleteByYm(ym);
        }
        calymRepo.flush();
        calyRepo.deleteByYr(yr);
        calyRepo.flush();

        Integer daysPerYear =  calYearFonds.values().stream()
                .mapToInt(Integer::intValue).sum();
        BigDecimal daysPerYearBd = BigDecimal.valueOf(daysPerYear);
        Caly caly = new Caly(yr, daysPerYearBd);
        caly = calyRepo.save(caly);

        for (Map.Entry<YearMonth, Integer> ymFondEntry : calYearFonds.entrySet()) {
            Calym calym = new Calym(ymFondEntry.getKey(), BigDecimal.valueOf(ymFondEntry.getValue()), caly);
            calymRepo.save(calym);
        }
    };


    @Override
    public Map<YearMonth, Integer> calcWorkDayCountsForYr(Integer yr)
    {
        Map<YearMonth, Integer> workDayCounts = new HashMap<>();
        List<LocalDate> holidays = fetchCalyHolDateListByYear(yr);
        int yrWorkDays = 0;
        for (int i = 1; i <= 12; i++) {
            int ymWorkDays = 0;
            YearMonth ym = YearMonth.of(yr, i);
            ymWorkDays += calcWorkDayCountForYm(ym, holidays);
            yrWorkDays += ymWorkDays;
            workDayCounts.put(ym, ymWorkDays);
        }
        return workDayCounts;
    }

    private int calcWorkDayCountForYm(YearMonth ym, List<LocalDate> holidays)
    {
        Calendar startCal;
        startCal = Calendar.getInstance();
        startCal.set(ym.getYear(), ym.getMonthValue() - 1, 1);

        Calendar endCal;
        endCal = Calendar.getInstance();
        endCal.set(ym.getYear(), ym.getMonthValue() - 1, 1);
        endCal.set(Calendar.DATE, endCal.getActualMaximum(Calendar.DATE));

        int monthWorkDays = 0;
        while (startCal.getTimeInMillis() < endCal.getTimeInMillis()) {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                    && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
                    && !holidays.contains((Integer) startCal.get(Calendar.DAY_OF_YEAR))) {
                ++monthWorkDays;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return monthWorkDays;
    }

    // Calendar - holiday
    // ------------------
    @Override
    public CalyHol fetchCalyHols(LocalDate holDate) {
        if (null == holDate) {
            return null;
        }
        return calyHolRepo.findByHolDate(holDate);
    }

    @Override
    public boolean calyHolÈxist(LocalDate holDate) {
        return null != fetchCalyHols(holDate);
    }

    @Override
    public Page<CalyHol> fetchCalyHolsByExample(Example<CalyHol> calyHolExample, Pageable pageable) {
//        if (null == yr) {
//            return null;
//        }
        return calyHolRepo.findAll(calyHolExample, pageable);
    }

    @Override
    public long countCalyHolsByExample(Example<CalyHol> calyHolExample, Pageable pageable) {
//        return calyHolRepo.findAll(calyHolExample, pageable).getSize();
        return calyHolRepo.findAll(calyHolExample, pageable).getTotalElements();
    }

    @Override
    public long countCalyHolsByYear(Integer yr) {
        return calyHolRepo.countByYr(yr);
    }

    private List<LocalDate> fetchCalyHolDateListByYear(Integer yr) {
        return new ArrayList<>();
    }
}
