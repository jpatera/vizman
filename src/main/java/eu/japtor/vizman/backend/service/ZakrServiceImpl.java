package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Mena;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.ZakrRepo;
import eu.japtor.vizman.backend.repository.ZaqaRepo;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;


@Service
public class ZakrServiceImpl implements ZakrService, HasLogger {

    private ZakrRepo zakrRepo;
    private ZaqaRepo zaqaRepo;

    @Autowired
    public ZakrServiceImpl(ZakrRepo zakrRepo, ZaqaRepo zaqaRepo) {
        super();
        this.zakrRepo = zakrRepo;
        this.zaqaRepo = zaqaRepo;
    }

    @Override
    @Transactional
    public Zakr saveZakr(Zakr zakrToSave, Operation oper) throws VzmServiceException {
        String kzCis = String.format("%s / %d", zakrToSave.getCkont(), zakrToSave.getCzak());
        try {
//            kontRepo.flush();
//            zakRepo.flush();
//            Zak zakSaved = zakRepo.saveAndFlush(zakToSave);
            Zakr zakrSaved = zakrRepo.save(zakrToSave);
            getLogger().info("{} saved: {} [operation: {}]"
                    , zakrSaved.getTyp().name(), kzCis, oper.name());
            return zakrSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
                getLogger().error(errMsg, zakrToSave.getTyp().name(), kzCis, oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    @Transactional
    public Zakr fetchOne(Long id) {
        Zakr zakr = zakrRepo.findTopById(id);
        Hibernate.initialize(zakr.getZaqas());
//      Or...:  return zakRepo.findById();
        return zakr;
    }

    @Override
    public Zakr fetchAndCalcOne(Long id, ZakrListView.ZakrParams zakrParams) {
        Zakr zakr = zakrRepo.findTopById(id);
        adjustZakrVykonAndVysledky.accept(zakr, zakrParams);
        return zakr;
    }

    @Override
    public List<Zakr> fetchAndCalcByActiveFilterDescOrder(ZakrListView.ZakrParams zakrParams) {
        List<Zakr> zakrs;
        if (zakrParams.isActive()) {
            zakrs =  zakrRepo.findZakrByActiveFilterOrderByCkontDescCzakDesc(zakrParams.isActive());
        } else  {
            zakrs =  zakrRepo.findAllByOrderByCkontDescCzakDesc();
        }
        zakrs.stream()
                .forEach(zr -> adjustZakrVykonAndVysledky.accept(zr, zakrParams));
        return zakrs;
    }

    @Override
    public List<Zakr> fetchAndCalcByFiltersDescOrder(ZakrListView.ZakrFilter zakrFilter, ZakrListView.ZakrParams zakrParams) {
        List<Zakr> zakrs = zakrRepo.findZakrByFilterParams(
                zakrParams.isActive()
                , zakrFilter.getArch()
                , zakrFilter.getCkz()
                , zakrFilter.getRokZak()
                , zakrFilter.getSkupina()
                , zakrFilter.getKzText()
                , zakrFilter.getObjednatel()
        );
        zakrs.stream()
                .forEach(zr -> adjustZakrVykonAndVysledky.accept(zr, zakrParams));
        return zakrs;
    }

    @Override
    public List<Zakr> fetchAndCalcAllDescOrder(ZakrListView.ZakrParams zakrParams) {
        List<Zakr> zakrs = zakrRepo.findAllByOrderByCkontDescCzakDesc();
        zakrs.stream()
                .forEach(zr -> adjustZakrVykonAndVysledky.accept(zr, zakrParams));
        return zakrs;
    }

    @Override
    public List<Long> fetchIdsByFiltersDescOrderWithLimit(ZakrListView.ZakrFilter zakrFilter, ZakrListView.ZakrParams zakrParams) {
        List<Long> zakrIds = zakrRepo.findIdsByFilterParamsWithLimit(
                zakrParams.isActive()
                , zakrFilter.getArch()
                , zakrFilter.getCkz()
                , zakrFilter.getRokZak()
                , zakrFilter.getSkupina()
                , zakrFilter.getKzText()
                , zakrFilter.getObjednatel()
        );
        return zakrIds;
    }


    private BiConsumer<Zakr, ZakrListView.ZakrParams> adjustZakrVykonAndVysledky = (zr, par) -> {
        if (zr.getMena() == Mena.EUR) {
            zr.setKurzEur(par.getKurzEur());
        }
        zr.setRxRyVykon(zr.calcRxRyVykon(par.getRx(), par.getRy()));
        zr.setVysledekByKurz(zr.calcVysledekByKurz(par.getKoefPojist(), par.getKoefRezie()));
        zr.setVysledekP8ByKurz(zr.calcVysledekP8ByKurz(par.getKoefPojist(), par.getKoefRezie()));
    };

    @Override
    public List<Zakr> fetchByRokDescOrder(final Integer rok) {
        return zakrRepo.findByRokOrderByCkontDescCzakDesc(rok);
    }

    @Override
    public long countAll() {
        return zakrRepo.count();
    }

    @Override
    public List<Integer> fetchZakrRoks() {
        return zakrRepo.findZakrRoks();
    }

    @Override
    @Transactional
    // Return  type void is intentional, did not succeed how to retrieve a complete Zakr inside transaction.
    // RP does not change, Zakr must be retrieved outside this transaction
    public void saveZakr(Zakr itemForSave) {
        String kzCis = String.format("%s / %d", itemForSave.getCkont(), itemForSave.getCzak());

        try {
            List<Zaqa> zaqasDb = zaqaRepo.findByZakrIdOrderByRokDesc(itemForSave.getId());
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (Iterator<Zaqa> i = zaqasDb.iterator(); i.hasNext();) {
                Zaqa zaqaDb = i.next();
                if ((null == zaqaDb.getRx())
                        || (zaqaDb.getRok().equals(currentYear))
                        || (zaqaDb.getRok().equals(currentYear - 1))
                        || ((zaqaDb.getRok().equals(currentYear - 2) && (zaqaDb.getQa() == 4)))
                    ) {
                    i.remove();
                }
            }
            if (null != itemForSave.getRm4()) {
                zaqasDb.add(new Zaqa(currentYear - 2, 4, itemForSave.getRm4(), itemForSave));
            }
            if (null != itemForSave.getRm3()) {
                zaqasDb.add(new Zaqa(currentYear - 1, 1, itemForSave.getRm3(), itemForSave));
            }
            if (null != itemForSave.getRm2()) {
                zaqasDb.add(new Zaqa(currentYear - 1, 2, itemForSave.getRm2(), itemForSave));
            }
            if (null != itemForSave.getRm1()) {
                zaqasDb.add(new Zaqa(currentYear - 1, 3, itemForSave.getRm1(), itemForSave));
            }
            if (null != itemForSave.getR0()) {
                zaqasDb.add(new Zaqa(currentYear - 1, 4, itemForSave.getR0(), itemForSave));
            }
            if (null != itemForSave.getR1()) {
                zaqasDb.add(new Zaqa(currentYear, 1, itemForSave.getR1(), itemForSave));
            }
            if (null != itemForSave.getR2()) {
                zaqasDb.add(new Zaqa(currentYear, 2, itemForSave.getR2(), itemForSave));
            }
            if (null != itemForSave.getR3()) {
                zaqasDb.add(new Zaqa(currentYear, 3, itemForSave.getR3(), itemForSave));
            }
            if (null != itemForSave.getR4()) {
                zaqasDb.add(new Zaqa(currentYear, 4, itemForSave.getR4(), itemForSave));
            }

            zaqaRepo.deleteAllByZakId(itemForSave.getId());
            zaqaRepo.saveAll(zaqasDb);
            zaqaRepo.flush();

            Zakr zakrFinal = zakrRepo.findTopById(itemForSave.getId());
            zakrFinal.setRm4(itemForSave.getRm4());
            zakrFinal.setRm3(itemForSave.getRm3());
            zakrFinal.setRm2(itemForSave.getRm2());
            zakrFinal.setRm1(itemForSave.getRm1());
            zakrFinal.setR0(itemForSave.getR0());
            zakrFinal.setR1(itemForSave.getR1());
            zakrFinal.setR2(itemForSave.getR2());
            zakrFinal.setR3(itemForSave.getR3());
            zakrFinal.setR4(itemForSave.getR4());
            zakrRepo.saveAndFlush(zakrFinal);

            getLogger().info("ZAKR saved for: {}" , kzCis);

        } catch (Exception e) {
            String errMsg = "Error while saving rozpracovanost for : {}";
            getLogger().error(errMsg, kzCis, e);
            throw new VzmServiceException(errMsg);
        }
    }
}
