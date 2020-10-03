package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Nab fetchOne(Long id) {
        return nabRepo.findTopById(id);
    }

    @Override
    public Nab saveNab(Nab itemToSave, Operation oper) {
        try {
            itemToSave.setDatetimeUpdate(LocalDateTime.now());
            Nab nabSaved = nabRepo.save(itemToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , nabSaved.getTyp().name(), oper.name());

            return nabSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteNab(Nab nabViewToDelete) {
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

    @Override
    public long getCountOfNabsWithObjednatel(Klient objednatel) {
        return nabRepo.countAllByObjednatel(objednatel);
    }
}
