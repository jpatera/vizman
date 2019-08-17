package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.ui.components.Operation;

import java.util.LinkedList;
import java.util.List;

public interface PersonWageService {

//    Person getById(Long id);
//
//    Person savePerson(Person person);
//
//    void deletePerson(Person person);
//
//    Person getByUsername(String username);

    PersonWage savePersonWage (PersonWage wageToSave, Operation oper);

    boolean hasValidDates(PersonWage personWageToSave);

    LinkedList<PersonWage> fetchByPersonId(Long personId);

    List<PersonWage> savePersonWageList(List<PersonWage> personWageList);

//    long countAll();

//    long countByFilter(String filter);
}
