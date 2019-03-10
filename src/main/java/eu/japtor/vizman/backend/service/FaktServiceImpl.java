package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.FaktRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaktServiceImpl implements FaktService, HasLogger {

    private FaktRepo faktRepo;

    @Autowired
    public FaktServiceImpl(FaktRepo faktRepo) {
        super();
        this.faktRepo = faktRepo;
    }

    @Override
    public Fakt saveFakt(Fakt fakt) {
        return faktRepo.save(fakt);
    }

    @Override
    public boolean deleteFakt(Fakt fakt) {
        try {
            faktRepo.delete(fakt);
        } catch (Exception e) {
            getLogger().error("Error while deleting subdodavka", e);
            return false;
        }
        return true;
    }

    @Override
    public Fakt getFakt(Long id) {
        return faktRepo.getOne(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public List<Fakt> fetchAll() {
        return faktRepo.findAll();
    }

    @Override
    public long countAll() {
        return faktRepo.count();
    }
}