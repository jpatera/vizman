package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Doch;
import eu.japtor.vizman.backend.repository.CalymRepo;
import eu.japtor.vizman.backend.repository.DochRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
public class DochServiceImpl implements DochService, HasLogger {

    @Autowired
    private DochRepo dochRepo;

    @Autowired
    private CalymRepo calymRepo;

    @Override
    public List<Doch> fetchDochForPersonAndDate(Long personId, LocalDate dochDate) {
        return dochRepo.findByPersonIdAndDochDateOrderByCdochDesc(personId, dochDate);
    }

    @Override
    public long countDochForPersonAndDate(Long personId, LocalDate dochDate) {
        return dochRepo.countByPersonIdAndDochDate(personId, dochDate);
    }


    @Override
    public LocalDate fetchPrevDochDate(Long personId, LocalDate dochDate) {
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
    public Doch openFirstRec(Doch firstDochRec) {
       return openDochRec(firstDochRec);
    }

    @Override
    @Transactional
    public Doch addSingleRec(Doch singleDochRec) {
        return addDochRec(singleDochRec);
    }

    @Override
    @Transactional
    public Doch closeLastRec(Doch lastDochRec) {
        return closeDochRec(lastDochRec);
    }

    @Override
    @Transactional
    public Doch closeRecAndOpenNew(Doch lastDochRec, Doch newDochRec) {
        closeDochRec(lastDochRec);
        return openDochRec(newDochRec);
    }

    private Doch openDochRec(Doch recToOpen) {
        if (null != recToOpen) {
            Integer cdoch;
            if (Cin.ATYP_FIX_CAS.equals(recToOpen.getCinAkceTyp())) {
                Integer firstCdoch = dochRepo.findFirstCdochForPersonAndDate(recToOpen.getPersonId(), recToOpen.getDochDate());
            cdoch = null == firstCdoch ? 0 : Math.min(0, firstCdoch -1);
            }  else {
                Integer lastCdoch = dochRepo.findLastCdochForPersonAndDate(recToOpen.getPersonId(), recToOpen.getDochDate());
                cdoch = null == lastCdoch ? 1 : Math.max(1, lastCdoch + 1);
            }
            recToOpen.setCdoch(cdoch);
            return dochRepo.save(recToOpen);
        }
        return null;
    }

    private Doch addDochRec(Doch recToAdd) {
        if (null != recToAdd) {
            Integer cdoch;
            Integer lastCdoch = dochRepo.findLastCdochForPersonAndDate(recToAdd.getPersonId(), recToAdd.getDochDate());
            cdoch = null == lastCdoch ? 1 : Math.max(1, lastCdoch + 1);
            recToAdd.setCdoch(cdoch);
            return dochRepo.save(recToAdd);
        }
        return null;
    }

    private Doch closeDochRec(Doch recToClose) {
        if (null != recToClose) {
            if (null != recToClose.getFromTime() && null != recToClose.getToTime()) {
//                recToClose.setDochDurationFromUI(Duration.between(recToClose.getFromTime(), recToClose.getToTime()));
                recToClose.setDochDur(Duration.between(recToClose.getFromTime(), recToClose.getToTime()));
            }
            return dochRepo.save(recToClose);
        }
        return null;
    }

    @Override
    @Transactional
    public void removeLastZkDochAndReopenPrev(Doch dochRecToRemove) {
        if (null == dochRecToRemove.getToTime()) {
            dochRepo.deleteById(dochRecToRemove.getId());
        }
        Doch prevZkDoch = dochRepo.findLastZkDochForPersonAndDate(dochRecToRemove.getPersonId(), dochRecToRemove.getDochDate());
        if (null != prevZkDoch) {
            prevZkDoch.setToTime(null);
//            prevZkDoch.setDochDurationFromUI(null);
            prevZkDoch.setDochDur(null);
            prevZkDoch.setToModifDatetime(null);
            prevZkDoch.setToManual(false);
            dochRepo.save(prevZkDoch);
        }
    }

    @Override
    @Transactional
    public void removeDochRec(final Long personId, final LocalDate dochDate, Cin.CinKod cinKod) {
        Doch dochRecToRemove = dochRepo.findTop1ByPersonIdAndDochDateAndCinCinKod(personId, dochDate, cinKod);
        if (null == dochRecToRemove) {
            if (Cin.CinKod.dp == cinKod) {
                dochRecToRemove = dochRepo.findTop1ByPersonIdAndDochDateAndCinCinKod(personId, dochDate, Cin.CinKod.dc);
            } else if (Cin.CinKod.dc == cinKod) {
                dochRecToRemove = dochRepo.findTop1ByPersonIdAndDochDateAndCinCinKod(personId, dochDate, Cin.CinKod.dp);
            }
        }
        if (null != dochRecToRemove) {
            dochRepo.deleteById(dochRecToRemove.getId());
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

    @Override
    public BigDecimal calcKoefP8(Long personId, YearMonth ym) {
        long nonParagDays = dochRepo.countNonParagDays(personId, ym.getYear(), ym.getMonthValue());
        BigDecimal ymFondReduced = BigDecimal.valueOf(8).multiply(BigDecimal.valueOf(nonParagDays));
        Long durNanosLong = dochRepo.sumNonParagDochDur(personId, ym.getYear(), ym.getMonthValue());
        if (null == durNanosLong) {
            return null;
        }
        return ymFondReduced.divide(durToDec(Duration.ofNanos(durNanosLong)), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal durToDecNulled(Duration dur) {
        BigDecimal durDec = durToDec(dur);
        return (durDec.compareTo(BigDecimal.ZERO) == 0) ? null : durDec;
    }

    public static BigDecimal durToDec(Duration dur) {
        if (null == dur) {
            return BigDecimal.ZERO;
        }
        Long hours  = dur.toHours();
        Long minutes = dur.toMinutes() - hours * 60;
        return BigDecimal.valueOf(hours).add(BigDecimal.valueOf((minutes % 60) / 60f));
    }

    public static BigDecimal durPracToDecRoundedNulled(Duration dur) {
        BigDecimal durDec = durPracToDecRounded(dur);
        return (durDec.compareTo(BigDecimal.ZERO) == 0) ? null : durDec;
    }

    public static BigDecimal durPracToDecRounded(Duration durPrac) {
        if (null == durPrac) {
            return null;
        }
        Long hours  = durPrac.toHours();
        Long minutes = durPrac.toMinutes() - hours * 60;
        BigDecimal hoursDecPart;

        if ((hours == 7 && minutes > 30) && (hours == 8 && minutes < 30)) {
            hours = 8L;
            hoursDecPart = BigDecimal.valueOf(0.0);
        } else {
            if (minutes >= 30) {
                hoursDecPart = BigDecimal.valueOf(0.5);
            } else {
                hoursDecPart = BigDecimal.ZERO;
            }
        }

        return BigDecimal.valueOf(hours).add(hoursDecPart);
    }

}
