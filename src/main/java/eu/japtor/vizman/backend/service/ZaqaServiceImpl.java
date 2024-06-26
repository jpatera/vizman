package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.backend.repository.ZaqaRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ZaqaServiceImpl extends AbstractSortableService implements ZaqaService, HasLogger {

    private final ZaqaRepo zaqaRepo;
    private final ZakRepo zakRepo;
    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("ymFrom", SortDirection.DESCENDING));

    @Autowired
    public ZaqaServiceImpl(ZaqaRepo zaqaRepo, ZakRepo zakRepo) {
        super();
        this.zaqaRepo = zaqaRepo;
        this.zakRepo = zakRepo;
    }

    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public List<Zaqa> fetchByParentId(Long parentId) {
        return zaqaRepo.findByZakrIdOrderByRokDesc(parentId);
    }



    @Override
    @Transactional
    public Zaqa saveItem(Zaqa itemToSave, Operation oper) {
        try {
//            Zaqa lastItem = zaqaRepo.findPersonLastWage(itemToSave.getZakr().getId());
            Zaqa itemSaved = zaqaRepo.save(itemToSave);
//            if (null != lastItem && null == lastItem.getYmTo()) {
//                lastItem.setYmTo(itemSaved.getYmFrom().minusMonths(1));
//            }
            getLogger().info("{} saved: [operation: {}]"
                    , itemSaved.getTyp().name(), oper.name());
            return itemSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteItem(Zaqa itemToDelete) {
        try {
            zaqaRepo.deleteById(itemToDelete.getId());
            getLogger().info("{} deleted: for user ID {}", itemToDelete.getTyp().name(), itemToDelete.getZakr().getId());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : for user ID {}";
            getLogger().error(errMsg, itemToDelete.getTyp().name(), itemToDelete.getZakr().getId(), e);
            return false;
        }
        return true;
    }


    @Override
    @Transactional
    public void deleteZaqas(Long idZak) throws VzmServiceException {
//        String zakEvidCis = String.format("%s / %d", zakToDel.getCkont(), zakToDel.getCzak());
        String kzCislo = zakRepo.getOne(idZak).getKzCislo();
        try {
            int zaquasCount = zaqaRepo.findByZakrId(idZak).size();
            if (zaquasCount > 0) {
                zaqaRepo.deleteAllByIdZak(idZak);
                getLogger().info("{} quartal records of zak {} deleted", zaquasCount, kzCislo);
            }
        } catch (Exception e) {
            String errMsg = "Error while deleting quartal records of zak {}";
            getLogger().error(errMsg, kzCislo, e);
            throw new VzmServiceException(errMsg);
        }
    }
}
