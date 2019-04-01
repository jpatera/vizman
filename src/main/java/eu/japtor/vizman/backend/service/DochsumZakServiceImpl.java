package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.bean.PruhZak;
import eu.japtor.vizman.backend.entity.AbstractGenIdEntity;
import eu.japtor.vizman.backend.entity.Dochsum;
import eu.japtor.vizman.backend.entity.DochsumZak;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.repository.DochRepo;
import eu.japtor.vizman.backend.repository.DochsumRepo;
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
    public List<DochsumZak> fetchDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public long countDochsumZaksForPersonAndYm(Long personId, YearMonth dsYm) {
//        int dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
        return dochsumZakRepo.countByPersonIdAndDsYm(personId, dsYm);
    }

//    @Override
//    public boolean updateDochsumZaksForPersonAndMonth(
//            Long personId
//            , YearMonth ym
//            , List<DochsumZak> dsZaks) {
//        return false;
//    }

    @Override
    @Transactional
//    public boolean updateDochsumZaksForPersonAndMonth(Long personId, YearMonth ym, List<DochsumZak> dsZaks) {
    public boolean updateDochsumZaksForPersonAndMonth(
            Long pruhPersonId
            , YearMonth pruhYm
            , int pruhDayMax
            , List<PruhZak> pruhZaks
    ) {

        try {

            PersonWage personWage = personWageRepo.findPersonWageForMonth(pruhPersonId, pruhYm);
            List<DochsumZak> dsZaksDb = fetchDochsumZaksForPersonAndYm(pruhPersonId, pruhYm);

            // Get zakId list for deletion:
            // ----------------------------
            List<Long> dsZakIdsDb = dsZaksDb.stream()
                    .map(dszDb -> dszDb.getZakId()).distinct().collect(Collectors.toList());
            List<Long> dsZakIdsPruh = pruhZaks.stream()
                    .map(zakPruh -> zakPruh.getZakId()).distinct().collect(Collectors.toList());
            List<Long> dsZakIdsToDelete = dsZakIdsDb.stream()
                    .filter(dszZakIdDb -> !dsZakIdsPruh.contains(dszZakIdDb))
                    .collect(Collectors.toList());

            // Get DochsumZak list for insert/update:
            // --------------------------------------
            List<DochsumZak> dsZaksToUpdate = new ArrayList<>();
            List<DochsumZak> dsZaksToInsert = new ArrayList<>();
            for (PruhZak pzak : pruhZaks) {
                Long pzakZakId = pzak.getZakId();
    //            pzak.getZakId()...
                for (int i = 1; i <= pruhDayMax; i++) {
                    BigDecimal newCellHod = pzak.getHod(i);
                    LocalDate pzakDate = LocalDate.of(pruhYm.getYear(), pruhYm.getMonth(), i);
                    DochsumZak dsZakDb = dsZaksDb.stream()
                            .filter(zakDb -> zakDb.getZakId().equals(pzakZakId)
                                    && zakDb.getDsDate().equals(pzakDate)
                            )
                            .findFirst().orElse(null);
                    if (null != dsZakDb) {
                        if (dsZakDb.getDszWorkPruh().compareTo(newCellHod) != 0) {
                            dsZakDb.setDszWorkPruh(newCellHod);
                            dsZaksToUpdate.add(dsZakDb);
                        }
                    } else {
                        if (null != newCellHod && newCellHod.compareTo(BigDecimal.ZERO) != 0) {
                            DochsumZak dsZakNew = new DochsumZak(
                                    pruhPersonId, pzakDate, pzakZakId, newCellHod, personWage.getWage());
                            dsZaksToInsert.add(dsZakNew);
                        }
                    }
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

            dochsumZakRepo.deleteZeroDochsumZaksByPruhAndZakIds(pruhYm, pruhPersonId);
            em.flush();

            return true;

        } catch (Exception e) {
            getLogger().error("Error when trying to save pruh", e);
            return false;
        }
    }

}
