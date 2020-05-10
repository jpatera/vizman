package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.NabView;
import eu.japtor.vizman.backend.service.NabViewService;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

public class NabFiltPagDataProvider
                extends FilterablePageableDataProvider<NabView, NabViewService.NabFilter>
{
    private final NabViewService nabViewService;

    private final NabViewService.NabFilter defaultFilter = NabViewService.NabFilter.getEmpty();
    private final NabView defaultProbe = NabView.getEmptyInstance();

    private final List<QuerySortOrder> defaultSortOrders = Arrays.asList(
            new QuerySortOrder("cnab", SortDirection.DESCENDING)
//            new QuerySortOrder("holDate", SortDirection.ASCENDING)
//            , new QuerySortOrder("ym", SortDirection.ASCENDING)
    );

    private final ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreNullValues();

    public NabFiltPagDataProvider(NabViewService nabViewService) {
        this.nabViewService = nabViewService;
    }

//    private Example<NabView> buildNabExample(final HierarchicalQuery query) {
//        return Example.of(query.getFilter().orElse(null), matcher);
//    }

//    private Example<CalyHol> buildCalyHolExample(final HierarchicalQuery query, final Integer yr) {
//        CalyHol probe = CalyHol.getEmptyInstance();
//        probe.setYr(yr);
//        return Example.of(probe, matcher);
//    }

    private static Sort.Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Sort.Order(queryOrder.getDirection() == SortDirection.ASCENDING
                ? Sort.Direction.ASC : Sort.Direction.DESC
                , queryOrder.getSorted());
    }

    @Override
    protected Page<NabView> fetchFromBackEnd(Query<NabView, NabViewService.NabFilter> query, Pageable pageable) {
        return nabViewService.fetchByNabFilter(getNabFilter(), query.getSortOrders(), pageable);
    }

    @Override
    protected int sizeInBackEnd(Query<NabView, NabViewService.NabFilter> query) {
        return (int) nabViewService.countByNabFilter(getNabFilter());
    }

    private NabViewService.NabFilter getNabFilter() {
        return getOptionalFilter().orElse(defaultFilter);
//        return "%" + filter + "%";
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }
}
