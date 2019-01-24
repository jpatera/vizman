package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.repository.KlientRepo;
import eu.japtor.vizman.backend.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class KlientServiceImpl  extends AbstractSortableService implements KlientService {

    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("name", SortDirection.ASCENDING));

    @Autowired
    KlientRepo klientRepo;

//    @Autowired
//    public KlientServiceImpl() {
//        super();
//    }

    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public Klient fetchKlientByName(String name) {
        return null;
    }

    @Override
    public List<Klient> fetchAll() {
        return klientRepo.findAllByOrderByName();
    }

    @Override
    public long countAll() {
        return klientRepo.count();
    }

    @Override
    public Klient saveKlient(Klient klient) {
        return klientRepo.saveAndFlush(klient);
    }

    @Override
    public void deleteKlient(Klient klient) {
        klientRepo.deleteById(klient.getId());
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
    public List<Klient> fetchBySearchFilter(String searchString, List<QuerySortOrder> sortOrders) {
        if (searchString == null) {
            return klientRepo.findAll(mapSortOrdersToSpring(sortOrders));
        } else {
            String likeFilter = "%" + searchString.toLowerCase() + "%";

            // TODO: Or may be sort of this way:
//            List<QuerySortOrder> defaultSort = ImmutableList.of(new QuerySortOrder("username", SortDirection.ASCENDING));
//            List<QuerySortOrder> defaultSort = Collections.singletonList(new QuerySortOrder("username", SortDirection.ASCENDING));
            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    .withIgnoreNullValues();

            return klientRepo.findByNameLikeIgnoreCase(likeFilter, mapSortOrdersToSpring(sortOrders));

            // Make a copy of each matching item to keep entities and DTOs separated
            //        return personRepo.findAllByUsername(filter).stream()
//            return fetchAll().stream()
//                    //                .filter(c -> c.getUsername().toLowerCase().contains(normalizedFilter))
//                    .filter(u -> u.getUsername().toLowerCase().contains(normalizedFilter))
//                    //              .map(Person::new)
//                    //                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
//                    .collect(Collectors.toList());
        }
    }

    @Override
    public long countByFilter(String filter) {
        if (filter == null) {
            return countAll();
        } else {
            String likeFilter = "%" + filter.toLowerCase() + "%";

            // Make a copy of each matching item to keep entities and DTOs separated
            //        return personRepo.findAllByUsername(filter).stream()
            return klientRepo.countByNameLikeIgnoreCase(likeFilter);

//            return fetchAll().stream()
//                    //                .filter(c -> c.getUsername().toLowerCase().contains(normalizedFilter))
//                    .filter(u -> u.getUsername().toLowerCase().contains(normalizedFilter))
//                    //              .map(Person::new)
//                    //                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
//                    .collect(Collectors.toList());
        }
    }
}
