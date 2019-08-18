package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.ui.components.Operation;

import java.util.LinkedList;
import java.util.List;

public interface WageService {

    PersonWage saveWage(PersonWage wageToSave, Operation oper);

    boolean deleteWage(PersonWage wage);

    boolean hasValidDates(PersonWage wageToSave, Long personId);

    List<PersonWage> fetchByPersonId(Long personId);

    List<PersonWage> saveWageList(List<PersonWage> wageList);

//    long countAll();

//    long countByFilter(String filter);
}
