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

    List<Person> fetchByPersonFilter(PersonFilter personFilter, List<QuerySortOrder> sortOrders);

    long countBySearchFilter(String searchFilter);

    long countByPersonFilter(PersonFilter personFilter);

    class PersonFilter {
        Boolean hidden;
        String username;

        public PersonFilter(Boolean hidden, String username) {
            this.hidden = hidden;
            this.username = username;
        }

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
