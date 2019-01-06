package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Zak saveZak(Zak zak) {
        return zakRepo.save(zak);
    }

    @Override
    public boolean deleteZak(Zak zak) {
        new OkDialog().open("Rušení zakázek není zatím implementováno", "", "");
        return false;
    }

    @Override
    public Zak getZak(Long id) {
        return zakRepo.getOne(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public List<Zak> getAllZak() {
        return zakRepo.findAll();
    }

    @Override
    public long countZak() {
        return zakRepo.count();
    }
}
