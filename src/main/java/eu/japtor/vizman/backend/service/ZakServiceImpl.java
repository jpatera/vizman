package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.ZakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZakServiceImpl implements ZakService {

    private ZakRepository zakRepository;

    @Autowired
    public ZakServiceImpl(ZakRepository zakRepository) {
        super();
        this.zakRepository = zakRepository;
    }

    @Override
    public Zak saveZak(Zak zak) {
        return zakRepository.save(zak);
    }

    @Override
    public Zak getZak(Long id) {
        return zakRepository.getOne(id);
    }

    @Override
    public List<Zak> getAllZak() {
        return zakRepository.findAll();
    }

    @Override
    public long countZak() {
        return zakRepository.count();
    }
}
