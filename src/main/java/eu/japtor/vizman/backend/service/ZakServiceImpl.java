package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.backend.repository.ZakrRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZakServiceImpl implements ZakService, HasLogger {

    private ZakRepo zakRepo;

    @Autowired
    private ZakrRepo zakrRepo;

    @Autowired
    public KontRepo kontRepo;

    @Autowired
    public ZakServiceImpl(ZakRepo zakRepo) {
        super();
        this.zakRepo = zakRepo;
    }


    @Override
    public Integer getNewCfakt(Zak zak) {
        return zak.getNewCfakt();
    }

    @Override
    public Integer getNewCfakt(Long zakId) {
        return getNewCfakt(zakRepo.getOne(zakId));
    }


    @Override
    public BigDecimal getSumPlneni(Long zakId) {
        return zakRepo.getOne(zakId).getSumPlneni();
    }

    @Override
    @Transactional
    public Zak saveZak(Zak zakToSave, Operation oper) throws VzmServiceException {
        String kzCis = String.format("%s / %d", zakToSave.getCkont(), zakToSave.getCzak());
        try {
//            kontRepo.flush();
//            zakRepo.flush();
//            Zak zakSaved = zakRepo.saveAndFlush(zakToSave);
            Zak zakSaved = zakRepo.save(zakToSave);
            getLogger().info("{} saved: {} [operation: {}]"
                    , zakSaved.getTyp().name(), kzCis, oper.name());
            return zakSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
                getLogger().error(errMsg, zakToSave.getTyp().name(), kzCis, oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

//    @Override
//    @Transactional
//    public Zakr saveZakr(Zakr zakrToSave, Operation oper) throws VzmServiceException {
//        String kzCis = String.format("%s / %d", zakrToSave.getCkont(), zakrToSave.getCzak());
//        Zak zakSaved = null;
//        try {
////            kontRepo.flush();
////            zakRepo.flush();
////            Zak zakSaved = zakRepo.saveAndFlush(zakToSave);
//            Zak zakToSave = zakRepo.getOne(zakrToSave.getId());
//            zakToSave.setRozprac(zakrToSave.getR0());
//            zakToSave.setR1(zakrToSave.getR1());
//            zakToSave.setR2(zakrToSave.getR2());
//            zakToSave.setR3(zakrToSave.getR3());
//            zakToSave.setR4(zakrToSave.getR4());
//            zakSaved = zakRepo.save(zakToSave);
//            getLogger().info("{} saved: {} [operation: {}]"
//                    , zakSaved.getTyp().name(), kzCis, oper.name());
//            return zakrRepo.getOne(zakrToSave.getId());
//        } catch (Exception e) {
//            String errMsg = "Error while saving {} : {} [operation: {}]";
//            getLogger().error(errMsg, null == zakSaved ? "N/A" : zakSaved.getTyp().name(), kzCis, oper.name(), e);
//            throw new VzmServiceException(errMsg);
//        }
//    }


    @Override
    @Transactional
    public void deleteZak(Zak zakToDel) throws VzmServiceException {
        String zakEvidCis = String.format("%s / %d", zakToDel.getCkont(), zakToDel.getCzak());
        try {
            zakRepo.delete(zakToDel);
            getLogger().info("{} deleted: {}", zakToDel.getTyp().name(), zakEvidCis);
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}";
            getLogger().error(errMsg, zakToDel.getTyp().name(), zakEvidCis, e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public Zak fetchOne(Long id) {
        return zakRepo.findTopById(id);
//        return zakRepo.getOne(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public boolean zakIdExistsInKont(Long idKont, Integer czak) {
        int cnt = zakRepo.getCountByIdKontAndCzak(idKont, czak);
//        Long cnt2 = zakRepo.countByKontIdAndCzak(idKont, czak);
        Long cnt2 = zakRepo.countByCzak(czak);
        return cnt > 0;
    }

    @Override
    public List<Zak> fetchAll() {
        return zakRepo.findAll();
    }

    @Override
    public List<Zak> fetchAllDescOrder() {
        return zakRepo.findAllByOrderByDateCreateDesc();
    }

    @Override
    public List<Zak> fetchByRokDescOrder(final Integer rok) {
        return zakRepo.findByRokOrderByRokDescIdDesc(rok);
    }

    @Override
    public List<Zak> fetchByIds(final List<Long> ids) {
        return zakRepo.findAllById(ids);
    };

    @Override
    public long countAll() {
        return zakRepo.count();
    }

    @Override
    public List<Integer> fetchZakRoks() {
        return zakRepo.findZakRoks();
    }
}
