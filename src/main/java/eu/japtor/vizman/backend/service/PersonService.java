package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonState;

import java.util.List;

public interface PersonService {

    Person getById(Long id);

    Person savePerson(Person person);

    void deletePerson(Person person);

    Person getByUsername(String username);

    List<Person> fetchAll();

    List<Person> fetchAllActive();

    long countAll();

    List<Person> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);
}
