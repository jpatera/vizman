package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.NabVw;
import eu.japtor.vizman.backend.service.NabViewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

public class NabFiltPagDataProvider
                extends FilterablePageableDataProvider<NabVw, NabViewService.NabViewFilter>
{
    private final NabViewService nabViewService;

    private final NabViewService.NabViewFilter defaultFilter = NabViewService.NabViewFilter.getEmpty();
    private final NabVw defaultProbe = NabVw.getEmptyInstance();

    private final List<QuerySortOrder> defaultSortOrders = Arrays.asList(
            new QuerySortOrder("cnab", SortDirection.DESCENDING)
//            new QuerySortOrder("holDate", SortDirection.ASCENDING)
//            , new QuerySortOrder("ym", SortDirection.ASCENDING)
    );

//    private final ExampleMatcher matcher = ExampleMatcher.matching()
//            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//            .withIgnoreNullValues();

    public NabFiltPagDataProvider(NabViewService nabViewService) {
        this.nabViewService = nabViewService;
    }

//    private Example<NabVw> buildNabExample(final HierarchicalQuery query) {
//        return Example.of(query.getFilter().orElse(null), matcher);
//    }

//    private Example<CalyHol> buildCalyHolExample(final HierarchicalQuery query, final Integer yr) {
//        CalyHol probe = CalyHol.getEmptyInstance();
//        probe.setYr(yr);
//        return Example.of(probe, matcher);
//    }

//    private static Sort.Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
//        return new Sort.Order(queryOrder.getDirection() == SortDirection.ASCENDING
//                ? Sort.Direction.ASC : Sort.Direction.DESC
//                , queryOrder.getSorted());
//    }

    @Override
    protected Page<NabVw> fetchFromBackEnd(Query<NabVw, NabViewService.NabViewFilter> query, Pageable pageable) {
        return nabViewService.fetchByNabFilter(getNabFilter(), query.getSortOrders(), pageable);
    }

    @Override
    protected int sizeInBackEnd(Query<NabVw, NabViewService.NabViewFilter> query) {
        return (int) nabViewService.countByNabFilter(getNabFilter());
    }

    private NabViewService.NabViewFilter getNabFilter() {
        return getOptionalFilter().orElse(defaultFilter);
//        return "%" + filter + "%";
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }
}
