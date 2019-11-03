package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonState;
import eu.japtor.vizman.ui.components.Operation;

import java.util.List;

public interface PersonService {

    Person fetchOne(Long id);

    Person savePerson(Person person, Operation oper);

    boolean deletePerson(Person person);

    Person getByUsername(String username);

    List<Person> fetchAll();

    List<Person> fetchAllNotHidden();

    long countAll();

    List<Person> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);
}
