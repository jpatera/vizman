package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
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
    public Zak saveZak(Zak zak, Operation oper) throws VzmServiceException {
        try {
            Zak zakSaved = zakRepo.save(zak);
            getLogger().info("{} saved: {} / {} [operation: {}]", zakSaved.getTyp().name()
                    , zakSaved.getCkont(), zakSaved.getCzak(), oper.name());

            return zakSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} / {} [operation: {}]";
            getLogger().error(errMsg, zak.getTyp().name(), zak.getCkont(), zak.getCzak(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    @Transactional
    public boolean deleteZak(Zak zak) {
        try {
            zakRepo.delete(zak);
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {} / {}";
            getLogger().error(errMsg, zak.getTyp().name(), zak.getCkont(), zak.getCzak(), e);
            return false;
        }
        return true;
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
