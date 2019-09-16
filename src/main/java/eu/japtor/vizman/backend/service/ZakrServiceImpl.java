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
    public List<Zakr> fetchAllDescOrder(ZakrListView.ZakrParams zakrParams) {
        List<Zakr> zakrs =  zakrRepo.findAllByOrderByRokDescCkontDescCzakDesc();
        zakrs.stream()
                .forEach(zr -> {
                    if (zr.getMena() == Mena.EUR) {
                        zr.setKurzEur(zakrParams.getKurzEur());
                    }
                    zr.setRxRyVykon(zr.calcRxRyVykon(zakrParams.getRx(), zakrParams.getRy()));
                    zr.setVysledekByKurz(zr.calcVysledekByKurz(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()));
                })
        ;
        return zakrs;
    }

    @Override
    public List<Zakr> fetchByFiltersDescOrder(ZakrListView.ZakrParams zakrParams) {
        List<Zakr> zakrs = zakrRepo.findZakrByArchAndRokAndSkupina(zakrParams.getArch(), zakrParams.getRokZak(), zakrParams.getSkupina());
//        zakrs.stream()
//                .filter(zr -> zr.getMena() == Mena.EUR)
//                .forEach(zr -> zr.setKurzEur(zakrParams.getKurzEur()))
//        ;
        zakrs.stream()
                .forEach(zr -> {
                    if (zr.getMena() == Mena.EUR) {
                        zr.setKurzEur(zakrParams.getKurzEur());
                    }
                    zr.setRxRyVykon(zr.calcRxRyVykon(zakrParams.getRx(), zakrParams.getRy()));
                    zr.setVysledekByKurz(zr.calcVysledekByKurz(zakrParams.getKoefPojist(), zakrParams.getKoefRezie()));
                })
        ;
        return zakrs;
    }


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
//            zakrRepo.flush();
//            Zak zakToSave = zakRepo.getOne(zakrToSave.getId());


//            Hibernate.initialize(itemForSave.getZaqas());
//            itemForSave.setZaqas(zaqaRepo.findByZakrIdOrderByRokDesc(itemForSave.getId()));

            List<Zaqa> zaqasDb = zaqaRepo.findByZakrIdOrderByRokDesc(itemForSave.getId());
//            List<Zaqa>zaqasToSave = (null == itemForSave.getZaqas()) ? new ArrayList<>() : itemForSave.getZaqas();
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

            for (Iterator<Zaqa> i = zaqasDb.iterator(); i.hasNext();) {
                Zaqa zaqaDb = i.next();
                if ((null == zaqaDb.getRx()) || ((zaqaDb.getRok().equals(currentYear))
                        || ((zaqaDb.getRok().equals(currentYear - 1) && zaqaDb.getQa() == 4)))
                        ) {
                    i.remove();
                }
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

//            zaqaRepo.deleteInBatch(itemForSave.getZaqas());
            zaqaRepo.deleteAllByZakId(itemForSave.getId());
            zaqaRepo.saveAll(zaqasDb);
            zaqaRepo.flush();

            Zakr zakrFinal = zakrRepo.findTopById(itemForSave.getId());
            zakrFinal.setR0(itemForSave.getR0());
            zakrFinal.setR1(itemForSave.getR1());
            zakrFinal.setR2(itemForSave.getR2());
            zakrFinal.setR3(itemForSave.getR3());
            zakrFinal.setR4(itemForSave.getR4());
            zakrRepo.saveAndFlush(zakrFinal);
//            Zakr zakr = zakrRepo.findTopById(itemForSave.getId());

            getLogger().info("ZAKR saved for: {}" , kzCis);
//            Zakr zakr = zakrRepo.findTopById(itemForSave.getId());
//            return zakrRepo.findTopById(itemForSave.getId());
//            return zakrRepo.findTopById(itemForSave.getId());

        } catch (Exception e) {
            String errMsg = "Error while saving rozpracovanost for : {}";
            getLogger().error(errMsg, kzCis, e);
            throw new VzmServiceException(errMsg);
        }
    }
}
