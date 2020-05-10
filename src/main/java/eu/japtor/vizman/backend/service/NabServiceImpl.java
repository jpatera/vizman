package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.NabView;
import eu.japtor.vizman.backend.repository.NabViewRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NabServiceImpl implements NabService, HasLogger {

    private NabViewRepo nabViewRepo;

    @Autowired
    public NabServiceImpl(NabViewRepo nabViewRepo) {
        super();
        this.nabViewRepo = nabViewRepo;
    }

    @Override
    public NabView fetchOne(Long id) {
        return nabViewRepo.findTopById(id);
    }

    @Override
    public NabView saveNab(NabView itemToSave, Operation oper) {
        try {
            NabView nabViewSaved = nabViewRepo.save(itemToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , nabViewSaved.getTyp().name(), oper.name());

            return nabViewSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteNab(NabView nabViewToDelete) {
        try {
            nabViewRepo.delete(nabViewToDelete);
            getLogger().info("{} deleted: {}, {}", nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}, {}";
            getLogger().error(errMsg, nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText(), e);
            return false;
        }
        return true;
    }
}
