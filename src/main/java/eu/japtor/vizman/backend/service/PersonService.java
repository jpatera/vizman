package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.ui.components.Operation;

import java.util.LinkedList;
import java.util.List;

public interface PersonService {

    Person fetchOne(Long id);

    Person savePerson(Person personToSave, Operation oper);

    boolean deletePerson(Person person);

    Person getByUsername(String username);

    List<Person> fetchAll();

    List<Person> fetchAllNotHidden();

    long countAll();

    List<Person> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);
    long countBySearchFilter(String searchFilter);

    List<Person> fetchByPersonFilter(PersonFilter personFilter, List<QuerySortOrder> sortOrders);
    long countByPersonFilter(PersonFilter personFilter);

    LinkedList<Long> fetchIdsByHidden(Boolean hidden);

    long countOfAssignedPerson(Long roleId);

    class PersonFilter {
        Boolean hidden = null;
        String username = null;

        public Boolean getHidden() {
            return hidden;
        }

        public void setHidden(Boolean hidden) {
            this.hidden = hidden;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
