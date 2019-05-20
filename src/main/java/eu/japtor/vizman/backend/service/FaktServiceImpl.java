package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.repository.FaktRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.OkDialog;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Fakt saveFakt(Fakt faktToSave, Operation oper) {
        String kzfCis = String.format("%s / %d / %d", faktToSave.getCkont(), faktToSave.getCzak(), faktToSave.getCfakt());
        try {
            Fakt faktSaved = faktRepo.save(faktToSave);
            getLogger().info("{} saved: {} [operation: {}]"
                    , faktSaved.getTyp().name(), kzfCis, oper.name());
            return faktSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
            getLogger().error(errMsg, faktToSave.getTyp().name(), kzfCis, oper.name(), e);
            throw new VzmServiceException(errMsg);
        }

    }

    @Override
    @Transactional
    public boolean deleteFakt(Fakt faktToDel) {
        String faktEvidCis = String.format("%s / %d / %d", faktToDel.getCkont(), faktToDel.getCzak(), faktToDel.getCfakt());
        try {
            faktRepo.deleteById(faktToDel.getId());
            getLogger().info("{} deleted: {}", faktToDel.getTyp().name(), faktEvidCis);
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}";
            getLogger().error(errMsg, faktToDel.getTyp().name(), faktEvidCis, e);
//            throw new VzmServiceException(errMsg);
            return false;
        }
        return true;
    }

//    @Override
//    public Fakt fetchOne(Long id) {
//        return faktRepo.findTopById(id).orElse(null);
////      Or...:  return zakRepo.findById();
//    }

    @Override
    public List<Fakt> fetchAll() {
        return faktRepo.findAll();
    }

    @Override
    public long countAll() {
        return faktRepo.count();
    }
}
