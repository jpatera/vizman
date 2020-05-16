package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.KontView;
import eu.japtor.vizman.backend.repository.KontRepo;
import eu.japtor.vizman.backend.repository.KontViewRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class KontServiceImpl extends AbstractSortableService implements KontService, HasLogger {

    private final KontRepo kontRepo;
    private final KontViewRepo kontViewRepo;

    private static final List<QuerySortOrder> DEFAULT_SORT_ORDER =
            Collections.singletonList(new QuerySortOrder("czak", SortDirection.ASCENDING));

    @Autowired
    public KontServiceImpl(KontRepo kontRepo, KontViewRepo kontViewRepo) {
        super();
        this.kontRepo = kontRepo;
        this.kontViewRepo = kontViewRepo;
    }


    @Override
    public List<QuerySortOrder> getDefaultSortOrders() {
        return DEFAULT_SORT_ORDER;
    }

    @Override
    public Kont fetchOne(Long id) {
//        return kontRepo.findById(id).orElse(null);
        return kontRepo.findTopById(id);
    }

    @Override
    public Kont fetchByCkont(String ckont) {
        return kontRepo.findTopByCkontIgnoreCase(ckont);
    }

    @Override
    public Kont fetchByText(String text) {
        return kontRepo.findTopByTextIgnoreCase(text);
    }

    @Override
    public Kont fetchByFolder(String folder) {
        return kontRepo.findTopByFolderIgnoreCase(folder);
    }

//    @Override
//    public Kont fetchByObjednatel(String objednatel) {
//        return kontRepo.findTopByObjednatelIgnoreCase(objednatel);
//    }

    @Override
    public List<Kont> fetchAll() {
        return kontRepo.findAllByOrderByCkontDescRokDesc();
    }

    @Override
    public List<KontView> fetchAllFromView() {
        return kontViewRepo.findAllByOrderByCkontDescRokDesc();
    }

    @Override
    public List<? super Kont> fetchByCkontFilter(final String ckont) {
        if (StringUtils.isBlank(ckont)) {
            return kontRepo.findAll();
        } else {
            Kont probe = Kont.getEmptyInstance();
//            probe.setHidden(personFilter.getHidden());
            probe.setCkont(ckont);
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("ckont", new ExampleMatcher.GenericPropertyMatcher().startsWith())
//                    .withMatcher("rok", new ExampleMatcher.GenericPropertyMatcher().exact())
//                    .withMatcher("arch", new ExampleMatcher.GenericPropertyMatcher().exact())
                    ;
            return kontRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public List<? super Kont> fetchByRokFilter(final Integer rok) {
        return kontRepo.findAllByRokOrderByCkontDesc(rok);
    }

    @Override
    public List<Kont> fetchHavingSomeZaksActiveFilter() {
        return kontRepo.findHavingSomeZaksActive();
    }

    @Override
    public List<Kont> fetchHavingAllZaksArchivedFilter() {
        return kontRepo.findHavingAllZaksArchived();
    }

    @Override
    public List<Kont> fetchHavingNoZaksFilter() {
        return kontRepo.findHavingNoZaks();
    }

    @Override
    public List<Integer> fetchKontRoks() {
        return kontRepo.findKontRoks();
    }

    @Override
    public long countAll() {
        return kontRepo.count();
    }

    @Override
    @Transactional
    public Kont saveKont(Kont kontToSave, Operation oper) throws VzmServiceException {
        try {
//            kontRepo.save(kont);
//            kontRepo.detachKont(kont);
//            kontRepo.flush();
//            Kont kontSaved = kontRepo.saveAndFlush(kontToSave);
            Kont kontSaved = kontRepo.save(kontToSave);
            getLogger().info("{} saved: {} [operation: {}]", kontSaved.getTyp().name()
                    , kontSaved.getCkont(), oper.name());
            return kontSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : {} [operation: {}]";
            getLogger().error(errMsg, kontToSave.getTyp().name(), kontToSave.getCkont(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    @Transactional
    public void deleteKont(Kont kontToDel) throws VzmServiceException {
        String kontEvidCis = String.format("%s", kontToDel.getCkont());
        try {
            kontRepo.delete(kontToDel);
//            kontRepo.flush();
            getLogger().info("{} deleted: {}", kontToDel.getTyp().name(), kontEvidCis);
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}";
            getLogger().error(errMsg, kontToDel.getTyp().name(), kontEvidCis, e);
            throw new VzmServiceException(errMsg);
        }
    }

//    /**
//     * Fetches the users whose name matches the given filter text.
//     *
//     * The matching is case insensitive. When passed an empty filter text,
//     * the method returns all users. The returned list is ordered
//     * by username.
//     *
//     * @param searchString    the filter text
//     * @return          the list of matching perosns
//     */
//    @Override
//    public List<Kont> fetchBySearchFilter(String searchString, List<QuerySortOrder> sortOrders) {
//        if (searchString == null) {
//            return kontRepo.findAll(mapSortOrdersToSpring(sortOrders));
//        } else {
//            String likeFilter = "%" + searchString.toLowerCase() + "%";
//
//            // TODO: Or may be sort of this way:
////            List<QuerySortOrder> defaultSort = ImmutableList.of(new QuerySortOrder("username", SortDirection.ASCENDING));
////            List<QuerySortOrder> defaultSort = Collections.singletonList(new QuerySortOrder("username", SortDirection.ASCENDING));
//            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
//                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                    .withIgnoreNullValues();
//
//            return kontRepo.findByObjednatelLikeIgnoreCase(likeFilter, mapSortOrdersToSpring(sortOrders));
//
//            // Make a copy of each matching item to keep entities and DTOs separated
//            //        return personRepo.findAllByUsername(filter).stream()
////            return fetchAll().stream()
////                    //                .filter(c -> c.getUsername().toLowerCase().contains(normalizedFilter))
////                    .filter(u -> u.getUsername().toLowerCase().contains(normalizedFilter))
////                    //              .map(Person::new)
////                    //                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
////                    .collect(Collectors.toList());
//        }
//    }

//    @Override
//    public long countByFilter(String filter) {
//        if (filter == null) {
//            return countAll();
//        } else {
//            String likeFilter = "%" + filter.toLowerCase() + "%";
//
//            // Make a copy of each matching item to keep entities and DTOs separated
//            //        return personRepo.findAllByUsername(filter).stream()
//            return kontRepo.countByObjednatelLikeIgnoreCase(likeFilter);
//
////            return fetchAll().stream()
////                    //                .filter(c -> c.getUsername().toLowerCase().contains(normalizedFilter))
////                    .filter(u -> u.getUsername().toLowerCase().contains(normalizedFilter))
////                    //              .map(Person::new)
////                    //                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
////                    .collect(Collectors.toList());
//        }
//    }

}
