package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.repository.PersonRepo;
import eu.japtor.vizman.backend.repository.PersonRoleRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class PersonServiceImpl extends AbstractSortableService implements PersonService, HasLogger {

    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("username", SortDirection.ASCENDING));

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private PersonRoleRepo personRoleRepo;

    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public Person fetchOne(Long id) {
        return personRepo.findTopById(id);
    }

    @Override
    public Person getByUsername(String username) {
        return personRepo.findTopByUsernameIgnoreCase(username);
    }

    @Override
    public List<Person> fetchAll() {
        return personRepo.findAllByOrderByUsername();
    }

    @Override
    public List<Person> fetchAllNotHidden() {
        return personRepo.findByHiddenOrderByUsername(false);
    }

    @Override
    public long countAll() {
        return personRepo.count();
    }

    @Override
    public Person savePerson(Person personToSave, Operation oper) {
        try {
            Person personSaved = personRepo.save(personToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , personSaved.getTyp().name(), oper.name());

            return personSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, personToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deletePerson(Person personToDelete) {
        try {
            personRepo.delete(personToDelete);
            getLogger().info("{} deleted: {}", personToDelete.getTyp().name(), personToDelete.getUsername());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : for user ID {}";
            getLogger().error(errMsg, personToDelete.getTyp().name(), personToDelete.getUsername(), e);
            return false;
        }
        return true;
    }

    /**
     * Fetches the users whose name matches the given filter text.
     *
     * The matching is case insensitive. When passed an empty filter text,
     * the method returns all users. The returned list is ordered
     * by username.
     *
     * @param searchString    the filter text
     * @return          the list of matching perosns
     */
    @Override
    public List<Person> fetchBySearchFilter(String searchString, List<QuerySortOrder> sortOrders) {
        if (searchString == null) {
            return personRepo.findAll(mapSortOrdersToSpring(sortOrders));
        } else {
            String likeFilter = "%" + searchString.toLowerCase() + "%";

//            // TODO: Or may be sort of this way:
////            List<QuerySortOrder> defaultSort = ImmutableList.of(new QuerySortOrder("username", SortDirection.ASCENDING));
////            List<QuerySortOrder> defaultSort = Collections.singletonList(new QuerySortOrder("username", SortDirection.ASCENDING));
//            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
//                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                    .withIgnoreNullValues();

            return personRepo.findByUsernameLikeIgnoreCase(likeFilter, mapSortOrdersToSpring(sortOrders));
        }
    }


    @Override
    public long countBySearchFilter(String searchfilter) {
        if (searchfilter == null) {
            return personRepo.count();
        } else {
            String likeFilter = "%" + searchfilter.toLowerCase() + "%";

            // Make a copy of each matching item to keep entities and DTOs separated
            //        return personRepo.findAllByUsername(filter).stream()
            return personRepo.countByUsernameLikeIgnoreCase(likeFilter);
        }
    }


    @Override
    public List<Person> fetchByPersonFilter(PersonFilter personFilter, List<QuerySortOrder> sortOrders) {
        if (personFilter == null) {
            return personRepo.findAll();
        } else {
            Person probe = Person.getEmptyInstance();
            probe.setHidden(personFilter.getHidden());
            probe.setUsername(personFilter.getUsername());

            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("username", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                    .withMatcher("hidden", new ExampleMatcher.GenericPropertyMatcher().exact())
                    ;
            return personRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public long countByPersonFilter(PersonFilter personFilter) {
        if (personFilter == null) {
            return personRepo.count();
        } else {
            Person probe = Person.getEmptyInstance();
            probe.setHidden(personFilter.getHidden());
            probe.setUsername(personFilter.getUsername());
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("username", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                    .withMatcher("hidden", new ExampleMatcher.GenericPropertyMatcher().exact())
                    ;
            return personRepo.count(Example.of(probe, matcher));
        }
    }

    @Override
    public LinkedList<Long> fetchIdsByHidden(final Boolean hidden) {
        return personRepo.findIdsByHidden(hidden);
    }

    @Override
    public long countOfAssignedPerson(Long roleId) {
        return personRoleRepo.countByRoleId(roleId);
    }
}
