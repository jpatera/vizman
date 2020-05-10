package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.NabView;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NabServiceImpl implements NabService, HasLogger {

    private NabRepo nabRepo;

    @Autowired
    public NabServiceImpl(NabRepo nabRepo) {
        super();
        this.nabRepo = nabRepo;
    }

    @Override
    public NabView fetchOne(Long id) {
        return nabRepo.findTopById(id);
    }

    @Override
    public NabView saveNab(NabView itemToSave, Operation oper) {
        try {
            NabView nabViewSaved = nabRepo.save(itemToSave);
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
            nabRepo.delete(nabViewToDelete);
            getLogger().info("{} deleted: {}, {}", nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}, {}";
            getLogger().error(errMsg, nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText(), e);
            return false;
        }
        return true;
    }
}
