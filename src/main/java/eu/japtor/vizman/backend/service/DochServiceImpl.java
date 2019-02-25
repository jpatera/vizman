package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class DochServiceImpl implements DochService, HasLogger {

    private DochRepo dochRepo;

    @Autowired
    public DochServiceImpl(DochRepo dochRepo) {
        super();
        this.dochRepo = dochRepo;
    }

    @Override
    public List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
        return dochRepo.findByPersonIdAndDochDateOrderByCdochDesc(personId, dochDate);
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
    @Transactional
    public Doch closePrevZkDochAndOpenNew(Doch newDoch) {

        LocalDateTime modifTime = LocalDateTime.now();
        closePrevZkDoch(newDoch, modifTime);

        Integer lastCdoch = dochRepo.findLastCdochForPersonAndDate(newDoch.getPersonId(), newDoch.getdDochDate());
        Integer nextCdoch = null == lastCdoch ? 1 : Math.max(1, lastCdoch + 1);
        newDoch.setCdoch(nextCdoch);
        newDoch.setFromModifDatetime(modifTime);
        return dochRepo.save(newDoch);
    }

    private void closePrevZkDoch(Doch newDoch, final LocalDateTime modifTime) {
        Doch prevZkDoch = dochRepo.findLastZkDochForPersonAndDate(newDoch.getPersonId(), newDoch.getdDochDate());
        if (null != prevZkDoch) {
            prevZkDoch.setToTime(newDoch.getFromTime());
            prevZkDoch.setToModifDatetime(modifTime);
            if (null != prevZkDoch.getFromTime() && null != newDoch.getFromTime()) {
                prevZkDoch.setDochDurationFromUI(Duration.between(prevZkDoch.getFromTime(), newDoch.getFromTime()));
                prevZkDoch.setDochDur(Duration.between(prevZkDoch.getFromTime(), newDoch.getFromTime()));
            }
        }
    }

    @Override
    @Transactional
    public Doch closeLastZkDoch(final Long personId, final LocalDate dochDate) {

        LocalDateTime modifTime = LocalDateTime.now();
        Doch lastZkDoch = dochRepo.findLastZkDochForPersonAndDate(personId, dochDate);
        if (null != lastZkDoch) {
            lastZkDoch.setToTime(modifTime.toLocalTime());
            lastZkDoch.setToModifDatetime(modifTime);
            if (null != lastZkDoch.getFromTime()) {
                lastZkDoch.setDochDurationFromUI(Duration.between(lastZkDoch.getFromTime(), lastZkDoch.getToTime()));
                lastZkDoch.setDochDur(Duration.between(lastZkDoch.getFromTime(), lastZkDoch.getToTime()));
            }
        }
        return dochRepo.save(lastZkDoch);
    }


    @Override
    @Transactional
    public void removeLastZkDochAndReopenPrev(Doch doch) {
        dochRepo.deleteById(doch.getId());
        Doch prevZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
        if (null != prevZkDoch) {
            prevZkDoch.setToTime(null);
            prevZkDoch.setDochDurationFromUI(null);
            prevZkDoch.setDochDur(null);
            prevZkDoch.setToModifDatetime(null);
            prevZkDoch.setToManual(false);
            dochRepo.save(prevZkDoch);
        }
    }


    @Override
    @Transactional
    public boolean removeAllDochRecsForPersonAndDate(final Long personId, final LocalDate dochDate) {

        if (dochRepo.countByPersonIdAndDochDate(personId, dochDate) <= 0) {
            getLogger().warn(String.format(
                    "Wanted to delete all DOCH recs for person ID %s an date %s but no recs found", personId, dochDate
            ));
            return false;
        }

        if (null != dochRepo.findTop1ByPersonIdAndDochDateAndDochState(personId, dochDate, Cin.ATYP_KONEC_CIN)
                || null != dochRepo.findTop1ByPersonIdAndDochDateAndDochState(personId, dochDate, Cin.ATYP_KONEC_DNE)) {
            getLogger().warn(String.format(
                    "Wanted to delete all DOCH recs for person ID %s an date %s but doch is not opened", personId, dochDate
            ));
            return false;
        }

        dochRepo.deleteByPersonIdAndDochDate(personId, dochDate);

        return true;
    }
}
