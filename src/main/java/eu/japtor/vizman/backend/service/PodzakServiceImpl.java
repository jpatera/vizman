package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Podzak;
import eu.japtor.vizman.backend.repository.PodzakRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PodzakServiceImpl implements PodzakService {

    private PodzakRepo podzakRepo;

    @Autowired
    public PodzakServiceImpl(PodzakRepo podzakRepo) {
        super();
        this.podzakRepo = podzakRepo;
    }

    @Override
    public Podzak savePodzak(Podzak podzak) {
        return podzakRepo.save(podzak);
    }

    @Override
    public Podzak getPodzak(Long id) {
        return podzakRepo.getOne(id);
//      Or...:  return podzakRepo.findById();
    }

    @Override
    public List<Podzak> getAllPodzak() {
        return podzakRepo.findAll();
    }

    @Override
    public long countPodzak() {
        return podzakRepo.count();
    }
}
