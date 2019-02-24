package eu.japtor.vizman.backend.service;

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

//    @Override
//    @Transactional
//    public Doch addFirstPrichod(Doch doch) {
//        return dochRepo.save(doch);
//    }

//    @Override
//    @Transactional
//    public Doch addPrichod(Doch doch) {
////        List<Doch> dochs = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
////        Doch lastZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
//        LocalDateTime modifTime = LocalDateTime.now();
//
////        if (dochs.size() != 1) {
////        Doch lastZkDoch = dochs.get(0);
////        if (null == lastZkDoch) {
////            lastZkDoch.setToTime(doch.getFromTime());
////            lastZkDoch.setToModifDatetime(modifTime);
////            if (null != lastZkDoch.getFromTime() && null != lastZkDoch.getToTime()) {
////                lastZkDoch.setDochDuration(Duration.between(lastZkDoch.getFromTime(), lastZkDoch.getToTime()));
////            }
////        }
//        finalizePrevZkDoch(doch, modifTime);
//
//        Integer lastCdoch = Math.max(1, dochRepo.findLastCdochForPersonAndDate(doch.getPersonId(), doch.getdDochDate()));
//        doch.setCdoch(null == lastCdoch ? 1 : lastCdoch++);
//        doch.setFromModifDatetime(modifTime);
//        return dochRepo.save(doch);
//    }

    @Override
    @Transactional
    public Doch addZkDochRec(Doch doch) {
//        List<Doch> dochs = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
//        Doch lastZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
//        if (dochs.size() != 1) {
        LocalDateTime modifTime = LocalDateTime.now();

//        Doch lastZkDoch = dochs.get(0);
//        if (null == lastZkDoch) {
//            // Previous ZK doch exist, let us close this record:
//            lastZkDoch.setToTime(doch.getFromTime());
//            lastZkDoch.setToModifDatetime(modifTime);
//            if (null != lastZkDoch.getFromTime() && null != lastZkDoch.getToTime()) {
//                lastZkDoch.setDochDuration(Duration.between(lastZkDoch.getFromTime(), lastZkDoch.getToTime()));
//            }
//        }

        finalizePrevZkDoch(doch, modifTime);

        Integer lastCdoch = dochRepo.findLastCdochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
        Integer nextCdoch = null == lastCdoch ? 1 : Math.max(1, lastCdoch + 1);
        doch.setCdoch(nextCdoch);
        doch.setFromModifDatetime(modifTime);
        return dochRepo.save(doch);
    }

    private void finalizePrevZkDoch(Doch doch, final LocalDateTime modifTime) {
        Doch prevZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
        if (null != prevZkDoch) {
            prevZkDoch.setToTime(doch.getFromTime());
            prevZkDoch.setToModifDatetime(modifTime);
            if (null != prevZkDoch.getFromTime() && null != doch.getFromTime()) {
                prevZkDoch.setDochDuration(Duration.between(prevZkDoch.getFromTime(), doch.getFromTime()));
            }
        }
    }


    @Override
    @Transactional
    public void removeLastZkDochRec(Doch doch) {
//        List<Doch> dochs = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
//        Doch lastZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
//        if (dochs.size() != 1) {
        LocalDateTime modifTime = LocalDateTime.now();

//        Doch lastZkDoch = dochs.get(0);
//        if (null == lastZkDoch) {
//            // Previous ZK doch exist, let us close this record:
//            lastZkDoch.setToTime(doch.getFromTime());
//            lastZkDoch.setToModifDatetime(modifTime);
//            if (null != lastZkDoch.getFromTime() && null != lastZkDoch.getToTime()) {
//                lastZkDoch.setDochDuration(Duration.between(lastZkDoch.getFromTime(), lastZkDoch.getToTime()));
//            }
//        }


        dochRepo.deleteById(doch.getId());

        Doch prevZkDoch = dochRepo.findLastZkDochForPersonAndDate(doch.getPersonId(), doch.getdDochDate());
        if (null != prevZkDoch) {
            prevZkDoch.setToTime(null);
            prevZkDoch.setDochDuration(null);
            prevZkDoch.setToModifDatetime(null);
            prevZkDoch.setToManual(false);
        }

        dochRepo.save(prevZkDoch);

//        reactivatePrevZkDoch(doch, modifTime);

    }

}
