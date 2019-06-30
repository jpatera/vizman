package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.backend.repository.ZakrRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ZakrServiceImpl implements ZakrService, HasLogger {

    private ZakrRepo zakrRepo;

    @Autowired
    public ZakrServiceImpl(ZakrRepo zakrRepo) {
        super();
        this.zakrRepo = zakrRepo;
    }

    @Override
    @Transactional
    public Zakr saveZakr(Zakr zakrToSave, Operation oper) throws VzmServiceException {
        String kzCis = String.format("%s / %d", zakrToSave.getCkont(), zakrToSave.getCzak());
        try {
//            kontRepo.flush();
//            zakRepo.flush();
//            Zak zakSaved = zakRepo.saveAndFlush(zakToSave);
            Zakr zakrSaved = zakrRepo.save(zakrToSave);
            getLogger().info("{} saved: {} [operation: {}]"
                    , zakrSaved.getTyp().name(), kzCis, oper.name());
            return zakrSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
                getLogger().error(errMsg, zakrToSave.getTyp().name(), kzCis, oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public Zakr fetchOne(Long id) {
        return zakrRepo.findTopById(id);
//        return zakRepo.getOne(id);
//      Or...:  return zakRepo.findById();
    }

    @Override
    public List<Zakr> fetchAllDescOrder() {
        return zakrRepo.findAllByOrderByRokDescCkontDescCzakDesc();
    }

    @Override
    public List<Zakr> fetchByRokDescOrder(final Integer rok) {
        return zakrRepo.findByRokOrderByCkontDescCzakDesc(rok);
    }

    @Override
    public long countAll() {
        return zakrRepo.count();
    }

    @Override
    public List<Integer> fetchZakRoks() {
        return zakrRepo.findZakRoks();
    }
}
