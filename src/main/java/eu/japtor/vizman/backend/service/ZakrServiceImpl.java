package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.ZakrRepo;
import eu.japtor.vizman.backend.repository.ZaqaRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Zakr fetchOne(Long id) {
        return zakrRepo.findTopById(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public List<Zakr> fetchAllDescOrder() {
        return zakrRepo.findAllByOrderByRokDescCkontDescCzakDesc();
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
    public Zakr saveZakr(Zakr itemForSave) {
        String kzCis = String.format("%s / %d", itemForSave.getCkont(), itemForSave.getCzak());

        try {
//            zakrRepo.flush();
//            Zak zakToSave = zakRepo.getOne(zakrToSave.getId());

            List<Zaqa>zaqasToSave = (null == itemForSave.getZaqas()) ? new ArrayList<>() : itemForSave.getZaqas();
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

            for (Iterator<Zaqa> i = zaqasToSave.iterator(); i.hasNext();) {
                Zaqa zaqa = i.next();
                if ((null == zaqa.getRx()) || ((zaqa.getRok().equals(currentYear))
                        || ((zaqa.getRok().equals(currentYear - 1) && zaqa.getQa() == 4)))
                        ) {
                    i.remove();
                }
            }
            if (null != itemForSave.getR0()) {
                zaqasToSave.add(new Zaqa(currentYear - 1, 4, itemForSave.getR0(), itemForSave));
            }
            if (null != itemForSave.getR1()) {
                zaqasToSave.add(new Zaqa(currentYear, 1, itemForSave.getR1(), itemForSave));
            }
            if (null != itemForSave.getR2()) {
                zaqasToSave.add(new Zaqa(currentYear, 2, itemForSave.getR2(), itemForSave));
            }
            if (null != itemForSave.getR3()) {
                zaqasToSave.add(new Zaqa(currentYear, 3, itemForSave.getR3(), itemForSave));
            }
            if (null != itemForSave.getR4()) {
                zaqasToSave.add(new Zaqa(currentYear, 4, itemForSave.getR4(), itemForSave));
            }

//            zaqaRepo.deleteInBatch(itemForSave.getZaqas());
            zaqaRepo.deleteAllByZakId(itemForSave.getId());
            zaqaRepo.saveAll(zaqasToSave);

            getLogger().info("ZAKR saved for: {}" , kzCis);
            Zakr zakr = zakrRepo.findTopById(itemForSave.getId());
//            return zakrRepo.findTopById(itemForSave.getId());
            return zakr;

        } catch (Exception e) {
            String errMsg = "Error while saving rozpracovanost for : {}";
            getLogger().error(errMsg, kzCis, e);
            throw new VzmServiceException(errMsg);
        }
    }
}
