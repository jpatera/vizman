package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ZakServiceImpl implements ZakService {

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
        new OkDialog().open("Rušení zakázek není implementováno", "", "");
        return false;
    }

    @Override
    public Zak getById(Long id) {
        return zakRepo.getOne(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public boolean zakIdExistsInKont(Long idKont, Integer czak) {
        return 0 > zakRepo.getCountByIdKontAndCzak(idKont, czak);
    }

    @Override
    public List<Zak> fetchAll() {
        return zakRepo.findAll();
    }

    @Override
    public long countAll() {
        return zakRepo.count();
    }
}
