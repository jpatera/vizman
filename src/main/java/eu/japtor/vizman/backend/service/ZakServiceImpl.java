package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Zak saveZak(Zak zak) {
        return zakRepo.save(zak);
    }

    @Override
    public boolean deleteZak(Zak zak) {
        try {
            zakRepo.delete(zak);
        } catch (Exception e) {
            getLogger().error("Error while deleting ZAKAZKA", e);
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
