package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.entity.YearMonthIntegerAttributeConverter;
import eu.japtor.vizman.backend.repository.PersonWageRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class PersonWageServiceImpl extends AbstractSortableService implements PersonWageService, HasLogger {

    private final PersonWageRepo personWageRepo;
    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("ymFrom", SortDirection.DESCENDING));

    @Autowired
    public PersonWageServiceImpl(PersonWageRepo personWageRepo) {
        super();
        this.personWageRepo = personWageRepo;
    }

    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public LinkedList<PersonWage> fetchByPersonId(Long personId) {
        return personWageRepo.findByPersonIdOrderByYmFromDesc(personId);
    }

    @Override
    @Transactional
    public PersonWage savePersonWage(PersonWage personWageToSave, Operation oper) {
        try {
            PersonWage personWageSaved = personWageRepo.save(personWageToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , personWageSaved.getTyp().name(), oper.name());
            return personWageSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, personWageToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    @Transactional
    public boolean hasValidDates(PersonWage wage) {
//        try {
//            Integer ymFromInt = ymConverter.convertToDatabaseColumn(wage.getYmFrom());
//            Integer ymToInt = ymConverter.convertToDatabaseColumn(wage.getYmTo());

            return 0 == personWageRepo.getCoincidingWages(
                    wage.getPersonId(), wage.getYmFrom(), wage.getYmTo());
//                    wage.getPerson().getId(), wage.getYmFrom(), wage.getYmTo());

//        } catch (Exception e) {
//            String errMsg = "Dates of wage are already occupied: FROM={} , TO={}";
//            getLogger().error(errMsg, wage.getYmFrom(), wage.getYmTo(), e);
//            throw new VzmServiceException(errMsg);
//        }
    }

    @Override
    public List<PersonWage> savePersonWageList(List<PersonWage> personWageList) {
        return null;
    }
}
