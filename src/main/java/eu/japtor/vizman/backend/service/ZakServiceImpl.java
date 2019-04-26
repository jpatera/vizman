package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ZakServiceImpl implements ZakService, HasLogger {

    private ZakRepo zakRepo;

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
            Zak zakSaved = zakRepo.saveAndFlush(zakToSave);
            getLogger().info("{} saved: {} [operation: {}]"
                    , zakSaved.getTyp().name(), kzCis, oper.name());
            return zakSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
                getLogger().error(errMsg, zakToSave.getTyp().name(), kzCis, oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }


    @Override
    @Transactional
    public void deleteZak(Zak zakToDel) throws VzmServiceException {
        String kzCis = String.format("%s / %d", zakToDel.getCkont(), zakToDel.getCzak());
        try {
            zakToDel.setKont(null);
            zakRepo.saveAndFlush(zakToDel);
//            Kont kontToSave = zakToDel.getKont();
//            kontToSave.removeZak(zakToDel);
//            kontRepo.saveAndFlush(kontToSave);
//            zakRepo.delete(zakToDel);
//            zakRepo.flush();
            getLogger().info("{} deleted: {}", zakToDel.getTyp().name(), kzCis);
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}";
            getLogger().error(errMsg, zakToDel.getTyp().name(), kzCis, e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public Zak getById(Long id) {
        return zakRepo.getOne(id);
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
    public List<Zak> fetchByIds(final List<Long> ids) {
        return zakRepo.findAllById(ids);
    };

    @Override
    public long countAll() {
        return zakRepo.count();
    }
}
