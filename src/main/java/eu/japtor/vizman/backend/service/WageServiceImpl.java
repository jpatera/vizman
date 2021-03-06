package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.repository.PersonWageRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class WageServiceImpl extends AbstractSortableService implements WageService, HasLogger {

    private final PersonWageRepo wageRepo;
    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("ymFrom", SortDirection.DESCENDING));

    @Autowired
    public WageServiceImpl(PersonWageRepo wageRepo) {
        super();
        this.wageRepo = wageRepo;
    }

    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public LinkedList<PersonWage> fetchByPersonId(Long personId) {
        return wageRepo.findByPersonIdOrderByYmFromDesc(personId);
    }

    @Override
    public List<PersonWage> saveWageList(List<PersonWage> wageList) {
        return null;
    }

    @Override
    @Transactional
    public PersonWage saveWage(PersonWage wageToSave, Operation oper) {
        try {

            PersonWage lastWage = wageRepo.findPersonLastWage(wageToSave.getPerson().getId());
            PersonWage wageSaved = wageRepo.save(wageToSave);
            if (null != lastWage && null != wageSaved && lastWage != wageSaved && null == lastWage.getYmTo()) {
                lastWage.setYmTo(wageSaved.getYmFrom().minusMonths(1));
            }
            getLogger().info("{} saved: [operation: {}]"
                    , wageSaved.getTyp().name(), oper.name());
            return wageSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, wageToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteWage(PersonWage wageToDelete) {
        try {
            wageRepo.deleteById(wageToDelete.getId());
            getLogger().info("{} deleted: for user ID {}", wageToDelete.getTyp().name(), wageToDelete.getPerson().getId());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : for user ID {}";
            getLogger().error(errMsg, wageToDelete.getTyp().name(), wageToDelete.getPerson().getId(), e);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean hasValidDates(PersonWage wageToSave, Long personId) {
//        try {
//            Integer ymFromInt = ymConverter.convertToDatabaseColumn(wage.getYmFrom());
//            Integer ymToInt = ymConverter.convertToDatabaseColumn(wage.getYmTo());

            return 0 == wageRepo.getCoincidingWages(
                    personId, wageToSave.getYmFrom(), wageToSave.getYmTo());
//                    wage.getPerson().getId(), wage.getYmFrom(), wage.getYmTo());

//        } catch (Exception e) {
//            String errMsg = "Dates of wage are already occupied: FROM={} , TO={}";
//            getLogger().error(errMsg, wage.getYmFrom(), wage.getYmTo(), e);
//            throw new VzmServiceException(errMsg);
//        }
    }
}
