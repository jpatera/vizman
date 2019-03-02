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
import java.time.LocalTime;
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


//    @Override
//    @Transactional
//    public Doch closePrevZkDochAndOpenNew(Doch newDoch) {
//        LocalDateTime modifTime = LocalDateTime.now();
//        stampOdchodAndNewOutsideRec(newDoch, modifTime);
//        return openDochRec(newDoch, modifTime);
//    }




//    @Override
//    @Transactional
//    public Doch stampOdchodAndNewOutsideRec(Doch lastInsideRec, Doch newOutsideRec) {
////        Doch lastZkDoch = dochRepo.findLastZkDochForPersonAndDate(dochOdchod.getPersonId(), dochOdchod.getdDochDate());
//        LocalDateTime modifdochStamp = LocalDateTime.now();
//        if (null != lastInsideRec) {
//            if (null == lastInsideRec.getToTime()) {
//                lastInsideRec.setToTime(dochStamp.toLocalTime());
//            }
//            Doch closedInsideRec = closeDochRec(lastInsideRec, dochStamp);
//            Doch newOutsideRec = new Doch(
//                    cinRepo.getByCinKod(outsideCinKod)
//                    , closedInsideRec.getPersonId()
//                    , closedInsideRec.getdDochDate()
//                    , dochStamp
//            );
//
//            return openDochRec(newOutsideRec, dochStamp);
//        }
//        return null;
//    }


    @Override
    @Transactional
    public Doch openFirstRec(Doch firstDochRec) {
       return openDochRec(firstDochRec);
    }

    @Override
    @Transactional
    public Doch closeLastRec(Doch lastDochRec) {
        return closeDochRec(lastDochRec);
    }

    @Override
    @Transactional
    public Doch closeLastRecAndOpenNew(Doch lastDochRec, Doch newDochRec) {
        closeDochRec(lastDochRec);
        return openDochRec(newDochRec);
    }

    private Doch openDochRec(Doch recToOpen) {
        if (null != recToOpen) {
            Integer lastCdoch = dochRepo.findLastCdochForPersonAndDate(recToOpen.getPersonId(), recToOpen.getdDochDate());
            Integer nextCdoch = null == lastCdoch ? 1 : Math.max(1, lastCdoch + 1);
            recToOpen.setCdoch(nextCdoch);
            return dochRepo.save(recToOpen);
        }
        return null;
    }

    private Doch closeDochRec(Doch recToClose) {
        if (null != recToClose) {
            if (null != recToClose.getFromTime() && null != recToClose.getToTime()) {
                recToClose.setDochDurationFromUI(Duration.between(recToClose.getFromTime(), recToClose.getToTime()));
                recToClose.setDochDur(Duration.between(recToClose.getFromTime(), recToClose.getToTime()));
            }
            return dochRepo.save(recToClose);
        }
        return null;
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
