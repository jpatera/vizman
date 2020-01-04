package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.DochsumZak;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.repository.DochsumZakRepo;
import eu.japtor.vizman.backend.repository.PersonWageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DochsumZakServiceImpl implements DochsumZakService, HasLogger {

    @PersistenceContext
    public EntityManager em;

    @Autowired
    private DochsumZakRepo dochsumZakRepo;

    @Autowired
    private PersonWageRepo personWageRepo;

//    @Autowired
//    public DochsumZakServiceImpl(DochsumZakRepo dochsumZakRepo) {
//        super();
//        this.dochsumZakRepo = dochsumZakRepo;
//    }

    @Override
    public YearMonth retrieveLastPruhYmForPerson(Long personId, YearMonth excludeYm) {
//        return dochsumZakRepo.getLastTwoPruhYmDochForPerson(personId, excludeYm);
//        return dochsumZakRepo.getTop1DsYmByPersonIdAndDsYmNotOrderByDsYmDesc(personId, excludeYm);
//        DochsumZak dsZak = dochsumZakRepo.getLastTwoPruhYmDochForPerson(personId, excludeYm);
        List<Integer> dsZak = dochsumZakRepo.getLastTwoPruhYmDochForPerson(personId);
//        return null == dsZak ? null : dsZak.getDsYm();
        if (null == dsZak || dsZak.size() == 0) {
            return null;
        }
        if (dsZak.size() == 1) {
            return YearMonth.of(dsZak.get(0) / 100, dsZak.get(0) % 100);
        } else {
            YearMonth ymNewer = YearMonth.of(dsZak.get(0) / 100, dsZak.get(0) % 100);
            YearMonth ymOlder = YearMonth.of(dsZak.get(1) / 100, dsZak.get(1) % 100);
            if (ymNewer.equals(excludeYm)) {
                return ymOlder;
            } else {
                return ymNewer;
            }
        }
    }

    @Override
    public List<DochsumZak> fetchDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public List<DochsumZak> fetchLatestDochsumZaks(Long zakId) {
        return dochsumZakRepo.findTop10ByZakIdOrderByDsDateDesc(zakId);
    }

    @Override
    public Long getCountDochsumZak(Long zakId) {
        return dochsumZakRepo.countByZakId(zakId);
    }

    @Override
    @Transactional
    public DochsumZak store(DochsumZak dsZak) {
        return dochsumZakRepo.save(dsZak);
    };

    @Override
    public long countDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.countByPersonIdAndDsYm(personId, dsYm);
    }

    @Autowired
    DochService dochService;

    @Override
    @Transactional
    public boolean updateDochsumZaksForPersonAndMonth(
            Long pruhPersonId
            , YearMonth pruhYm
            , int pruhDayMax
            , List<PruhZak> pruhZaks
    ) {

        try {

            PersonWage personWage = personWageRepo.findPersonWageForMonth(pruhPersonId, pruhYm);
            List<DochsumZak> dsZaksDb = fetchDochsumZaksForPersonAndYm(pruhPersonId, pruhYm);
            BigDecimal koefP8 = dochService.calcKoefP8(pruhPersonId, pruhYm);

            // Get zakId list for deletion:
            // ----------------------------
            List<Long> dsZakIdsDb = dsZaksDb.stream()
                    .map(dszDb -> dszDb.getZakId()).distinct().collect(Collectors.toList());
            List<Long> dsZakIdsPruh = pruhZaks.stream()
                    .map(zakPruh -> zakPruh.getZakId()).distinct().collect(Collectors.toList());
            List<Long> dsZakIdsToDelete = dsZakIdsDb.stream()
                    .filter(zakIdDb -> !dsZakIdsPruh.contains(zakIdDb))
                    .collect(Collectors.toList());

            // Get DochsumZak list for insert/update:
            // --------------------------------------
            List<DochsumZak> dsZaksToUpdate = new ArrayList<>();
            List<DochsumZak> dsZaksToInsert = new ArrayList<>();
//            List<DochsumZak> dsZaksToDelete = new ArrayList<>();
            for (PruhZak pzak : pruhZaks) {
                Long pzakZakId = pzak.getZakId();
    //            pzak.getZakId()...

                DochsumZak dsZakDbAnchor = dsZaksDb.stream()
                        .filter(zakDb -> zakDb.getZakId().equals(pzakZakId)
                                && zakDb.getDsYm().equals(pruhYm))
                        .findAny().orElse(null);
                if (null == dsZakDbAnchor) {
                    LocalDate pzakDateAnchor = pruhYm.atDay( 1 ).minusYears(200);
                    DochsumZak dsZakYmAnchor = new DochsumZak(
                            pruhPersonId, pzakDateAnchor, pruhYm, pzakZakId, null, null, null, null);
                    dsZaksToInsert.add(dsZakYmAnchor);
                    dsZaksDb.add(dsZakYmAnchor);
                }

                for (int i = 1; i <= pruhDayMax; i++) {
                    BigDecimal newCellHod = pzak.getHod(i);
                    LocalDate pzakDate = LocalDate.of(pruhYm.getYear(), pruhYm.getMonth(), i);
                    DochsumZak dsZakDb = dsZaksDb.stream()
                            .filter(zakDb -> zakDb.getZakId().equals(pzakZakId)
                                    && zakDb.getDsDate().equals(pzakDate))
                            .findFirst().orElse(null);
//                    if (i == 1) {
//                        if (null == newCellHod){
//                            newCellHod = BigDecimal.ZERO;
//                        }
//                        if (null != dsZakDb) {
//                            dsZakDb.setDszWorkPruh(newCellHod);
//                            dsZakDb.setSazba(personWage.getTariff());
//                            dsZakDb.setDszWorkPruh(dsZakDb.getSazba().multiply(dsZakDb.getDszWorkPruh()));
//                            dsZaksToUpdate.add(dsZakDb);
//                        } else {
//                            DochsumZak dsZakNew = new DochsumZak(
//                                    pruhPersonId, pzakDate, pzakZakId, newCellHod, personWage.getTariff());
//                            dsZaksToInsert.add(dsZakNew);
//                        }
//                    } else {
                        if (null != dsZakDb) {
                            if ((null == newCellHod) || newCellHod.compareTo(BigDecimal.ZERO) == 0) {
                                dsZakDb.setDszWorkPruh(null);
                                dsZakDb.setDszKoefP8(null);
                                dsZakDb.setDszWorkP8(null);
                            } else {
                                dsZakDb.setDszWorkPruh(newCellHod);
                                dsZakDb.setSazba(personWage.getTariff());
                                if (null == dsZakDb.getDszWorkPruh() || (null == dsZakDb.getSazba())) {
                                    dsZakDb.setDszMzda(null);
                                    dsZakDb.setDszMzdaP8(null);
                                } else {
                                    dsZakDb.setDszMzda(dsZakDb.getDszWorkPruh().multiply(dsZakDb.getSazba()));
                                    dsZakDb.setDszMzdaP8(dsZakDb.getDszWorkPruh().multiply(dsZakDb.getSazba()).multiply(koefP8));
                                }

                                dsZakDb.setDszKoefP8(koefP8);
                                dsZakDb.setDszWorkP8(newCellHod.multiply(koefP8));

                            }
                            dsZaksToUpdate.add(dsZakDb);
                        } else {
//                            if (i == 1) {
//                                LocalDate pzakDateAnchor = pzakDate.minusYears(100);
//                                DochsumZak dsZakYmAnchor = new DochsumZak(
//                                        pruhPersonId, pzakDateAnchor, pzakZakId, newCellHod, personWage.getTariff());
//                                dsZaksToInsert.add(dsZakYmAnchor);
//                            }
                            if (null != newCellHod && newCellHod.compareTo(BigDecimal.ZERO) != 0) {
                                DochsumZak dsZakNew = new DochsumZak(
                                        pruhPersonId, pzakDate, pruhYm, pzakZakId
                                        , newCellHod, koefP8, newCellHod.multiply(koefP8), personWage.getTariff());
                                dsZaksToInsert.add(dsZakNew);
                            }
                        }
//                    }
                }
            }

//            List<Long> dsZakIds = dsZaksToSave.stream()
//                    .map(dsz -> dsz.getId())
//                    .collect(Collectors.toList())
//                    ;

//            List<Long> dsZakDbIds = dochsumZakRepo.findByPersonIdAndDsYm(personId, ym).stream()
//                    .map(dszc -> dszc.getId())
//                    .collect(Collectors.toList())
//                    ;

//            List<DochsumZak> dsZaksToInsert = dsZaksToSave.stream()
//                    .filter(dsz -> null == dsz.getId())
//                    .collect(Collectors.toList())
//                    ;

//            List<DochsumZak> dsZaksToUpdate = dsZaksToSave.stream()
//                    .filter(dsz -> null != dsz.getId())
//                    .collect(Collectors.toList())
//                    ;

//            List<Long> dsZakIdsToDelete = dsZakDbIds.stream()
//                    .filter(dbid -> !dsZakIds.contains(dbid))
//                    .collect(Collectors.toList())
//                    ;


            // Perform updates and deletes:
            // ----------------------------

            if (!CollectionUtils.isEmpty(dsZakIdsToDelete)) {
                dochsumZakRepo.deleteDochsumZaksByPruhAndZakIds(pruhYm, pruhPersonId, dsZakIdsToDelete);
                em.flush();
            }

            if (!CollectionUtils.isEmpty(dsZaksToUpdate)) {
                dochsumZakRepo.saveAll(dsZaksToUpdate);
                em.flush();
            }

            if (!CollectionUtils.isEmpty(dsZaksToInsert)) {
                dochsumZakRepo.saveAll(dsZaksToInsert);
                em.flush();
            }

            dochsumZakRepo.deleteZeroDochsumZaksByPruhYmAndPerson(pruhYm, pruhPersonId);
            em.flush();

            return true;

        } catch (Exception e) {
            getLogger().error("Error when trying to save pruh", e);
            return false;
        }
    }

    @Override
    @Transactional
    public void recalcMzdyForPerson(Long personId) {
        dochsumZakRepo.adjustSazbaByPerson(personId);
        dochsumZakRepo.adjustMzdaByPerson(personId);
    }

}
