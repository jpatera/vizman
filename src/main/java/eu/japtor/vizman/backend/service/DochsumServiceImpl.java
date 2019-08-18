package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Service
public class DochsumServiceImpl implements DochsumService, HasLogger {

    @Autowired
    public DochsumRepo dochsumRepo;

    @Autowired
    public DochRepo dochRepo;

    @Autowired
    public DochsumZakRepo dochsumZakRepo;

    @Autowired
    public DochsumParagRepo dochsumParagRepo;

    @Autowired
    public PersonWageRepo personWageRepo;

    @Autowired
    public ZakRepo zakRepo;

    @Autowired
    public ParagRepo paragRepo;


//    @Autowired
//    public DochsumServiceImpl(DochsumRepo dochsumRepo) {
//        super();
//        this.dochsumRepo = dochsumRepo;
//    }

    @Override
    public List<Dochsum> fetchDochsumForPersonAndYm(Long personId, YearMonth dsYm) {
//        return dochRepo.findByPersonIdAndDochDateOrderByFromTimeDesc(personId, dochDate);
//        return dochRepo.findDochForPersonAndDate(personId, dochDate);
//        Integer dsYmInt = 100 * dsYm.getYear() + dsYm.getMonthValue();
//        return dochsumRepo.findByPersonIdAndDsYm(personId, dsYmInt);
        return dochsumRepo.findByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    public long countDochsumForPersonAndDate(Long personId, YearMonth dsYm) {
        return dochsumRepo.countByPersonIdAndDsYm(personId, dsYm);
    }

    @Override
    @Transactional
    public void deleteByDsDateAndPersonId(final LocalDate dochDate, final Long personId) {
        dochsumRepo.deleteByDsDateAndPersonId(dochDate, personId);
    }

    @Override
    @Transactional
    public void updateDochsumCloseDoch(final LocalDate dochDate, final Long personId, final Dochsum dochsum) {
        updateDochsum(dochDate, personId, dochsum);
        updateDochsumZakByLekar(dochDate, personId, dochsum.getDsLek());
        updateDochsumParagByDov(dochDate, personId, dochsum.getDsDov());
        updateDochsumParagByNem(dochDate, personId, dochsum.getDsNem());
        updateDochsumParagByVol(dochDate, personId, dochsum.getDsVol());
//        Doch dochPracLast = dochRepo.findLastZkDochForPersonAndDate(personId, dochDate);
        Doch dochPracLast = dochRepo.findLastPracDochForPersonAndDateNotOa(personId, dochDate);
        if (null != dochPracLast) {
            dochPracLast.setDochState(Doch.STATE_KONEC);
            dochRepo.save(dochPracLast);
        }
    }

    private void updateDochsumZakByLekar (final LocalDate dsDate, final Long personId, BigDecimal durDecLekRounded) {

        PersonWage personWage = personWageRepo.findPersonWageForMonth(personId, YearMonth.from(dsDate));
        Zak lekZak = zakRepo.findTop1ByTyp(ItemType.LEK);
        DochsumZak dsZakDb = dochsumZakRepo.findTop1ByPersonIdAndDsDateAndZakId(personId, dsDate, lekZak.getId());
        if (null != dsZakDb) {
            if ((null == durDecLekRounded) || durDecLekRounded.compareTo(BigDecimal.ZERO) == 0) {
                dsZakDb.setDszWorkPruh(null);
            } else if (dsZakDb.getDszWorkPruh().compareTo(durDecLekRounded) != 0) {
                dsZakDb.setDszWorkPruh(durDecLekRounded);
            }
            dochsumZakRepo.save(dsZakDb);
        } else {
            if (null != durDecLekRounded && durDecLekRounded.compareTo(BigDecimal.ZERO) != 0) {
                DochsumZak dsZakNew = new DochsumZak(
                        personId, dsDate, lekZak.getId(), durDecLekRounded, personWage.getTariff());
                dochsumZakRepo.save(dsZakNew);
            }
        }
    }

    private void updateDochsumParagByDov(final LocalDate dsDate, final Long personId, BigDecimal durDecDovRounded) {
        Parag dovParag = paragRepo.findTop1ByCparag(Parag.CPARAG_DOV);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, dovParag.getId());
        if (null != dsParagDb) {
            if ((null == durDecDovRounded) || durDecDovRounded.compareTo(BigDecimal.ZERO) == 0) {
                dsParagDb.setDspWorkOff(null);
            } else if (dsParagDb.getDspWorkOff().compareTo(durDecDovRounded) != 0) {
                dsParagDb.setDspWorkOff(durDecDovRounded);
            }
            dochsumParagRepo.save(dsParagDb);
        } else {
            if (null != durDecDovRounded && durDecDovRounded.compareTo(BigDecimal.ZERO) != 0) {
                DochsumParag dsParagNew = new DochsumParag(
                        personId, dsDate, dovParag.getId(), durDecDovRounded);
                dochsumParagRepo.save(dsParagNew);
            }
        }
    }

    private void updateDochsumParagByNem(final LocalDate dsDate, final Long personId, BigDecimal durDecNemRounded) {
        Parag nemParag = paragRepo.findTop1ByCparag(Parag.CPARAG_NEM);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, nemParag.getId());
        if (null != dsParagDb) {
            if ((null == durDecNemRounded) || durDecNemRounded.compareTo(BigDecimal.ZERO) == 0) {
                dsParagDb.setDspWorkOff(null);
            } else if (dsParagDb.getDspWorkOff().compareTo(durDecNemRounded) != 0) {
                dsParagDb.setDspWorkOff(durDecNemRounded);
            }
            dochsumParagRepo.save(dsParagDb);
        } else {
            if (null != durDecNemRounded && durDecNemRounded.compareTo(BigDecimal.ZERO) != 0) {
                DochsumParag dsParagNew = new DochsumParag(
                        personId, dsDate, nemParag.getId(), durDecNemRounded);
                dochsumParagRepo.save(dsParagNew);
            }
        }
    }

    private void updateDochsumParagByVol(final LocalDate dsDate, final Long personId, BigDecimal durDecVolRounded) {
        Parag volParag = paragRepo.findTop1ByCparag(Parag.CPARAG_VOL);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, volParag.getId());
        if (null != dsParagDb) {
            if ((null == durDecVolRounded) || durDecVolRounded.compareTo(BigDecimal.ZERO) == 0) {
                dsParagDb.setDspWorkOff(null);
            } else if (dsParagDb.getDspWorkOff().compareTo(durDecVolRounded) != 0) {
                dsParagDb.setDspWorkOff(durDecVolRounded);
            }
            dochsumParagRepo.save(dsParagDb);
        } else {
            if (null != durDecVolRounded && durDecVolRounded.compareTo(BigDecimal.ZERO) != 0) {
                DochsumParag dsParagNew = new DochsumParag(
                        personId, dsDate, volParag.getId(), durDecVolRounded);
                dochsumParagRepo.save(dsParagNew);
            }
        }
    }

    private void deleteLekarFromDochsumZak (final LocalDate dsDate, final Long personId) {
        Zak lekZak = zakRepo.findTop1ByTyp(ItemType.LEK);
        DochsumZak dsZakDb = dochsumZakRepo.findTop1ByPersonIdAndDsDateAndZakId(personId, dsDate, lekZak.getId());
        if (null != dsZakDb) {
            dochsumZakRepo.delete(dsZakDb);
        }
    }

    private void deleteNemocFromDochsumParag (final LocalDate dsDate, final Long personId) {
        Parag nemParag = paragRepo.findTop1ByCparag(Parag.CPARAG_NEM);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, nemParag.getId());
        if (null != dsParagDb) {
            dochsumParagRepo.delete(dsParagDb);
        }
    }

    private void deleteDovolenaFromDochsumParag (final LocalDate dsDate, final Long personId) {
        Parag dovParag = paragRepo.findTop1ByCparag(Parag.CPARAG_DOV);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, dovParag.getId());
        if (null != dsParagDb) {
            dochsumParagRepo.delete(dsParagDb);
        }
    }

    private void deleteVolnoFromDochsumParag (final LocalDate dsDate, final Long personId) {
        Parag volParag = paragRepo.findTop1ByCparag(Parag.CPARAG_VOL);
        DochsumParag dsParagDb = dochsumParagRepo.findTop1ByPersonIdAndDsDateAndParagId(personId, dsDate, volParag.getId());
        if (null != dsParagDb) {
            dochsumParagRepo.delete(dsParagDb);
        }
    }

    @Override
    @Transactional
    public void  deleteDochsumOpenDoch(final LocalDate dochDate, final Long personId) {
        dochsumRepo.deleteByDsDateAndPersonId(dochDate, personId);
        deleteLekarFromDochsumZak(dochDate, personId);
        deleteNemocFromDochsumParag(dochDate, personId);
        deleteDovolenaFromDochsumParag(dochDate, personId);
        deleteVolnoFromDochsumParag(dochDate, personId);

        Doch dochOa = dochRepo.findTop1ByPersonIdAndDochDateAndCinCinKod(personId, dochDate, Cin.CinKod.OA);
        if (null != dochOa) {
            dochRepo.delete(dochOa);
        }

        Doch dochLastPrac = dochRepo.findLastPracDochForPersonAndDateNotOa(personId, dochDate);
        if (null != dochLastPrac) {
            dochLastPrac.setDochState(Doch.STATE_NONE);
            dochRepo.save(dochLastPrac);
        }
    }

    private void updateDochsum(final LocalDate dochDate, final Long personId, final Dochsum dochsum) {
        dochsumRepo.deleteByDsDateAndPersonId(dochDate, personId);
        dochsumRepo.flush();
        dochsumRepo.save(dochsum);
    }

}
