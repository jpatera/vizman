package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepo;
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
    public Zak getZak(Long id) {
        return zakRepo.getOne(id);
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
