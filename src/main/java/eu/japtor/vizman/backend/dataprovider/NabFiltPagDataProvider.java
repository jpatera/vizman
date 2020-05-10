package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.service.NabService;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

public class NabFiltPagDataProvider
                extends FilterablePageableDataProvider<Nab, NabService.NabFilter>
{
    private final NabService nabService;

    private final NabService.NabFilter defaultFilter = NabService.NabFilter.getEmpty();
    private final Nab defaultProbe = Nab.getEmptyInstance();

    private final List<QuerySortOrder> defaultSortOrders = Arrays.asList(
            new QuerySortOrder("cnab", SortDirection.DESCENDING)
//            new QuerySortOrder("holDate", SortDirection.ASCENDING)
//            , new QuerySortOrder("ym", SortDirection.ASCENDING)
    );

    private final ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreNullValues();

    public NabFiltPagDataProvider(NabService nabService) {
        this.nabService = nabService;
    }

//    private Example<Nab> buildNabExample(final HierarchicalQuery query) {
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
    protected Page<Nab> fetchFromBackEnd(Query<Nab, NabService.NabFilter> query, Pageable pageable) {
        return nabService.fetchByNabFilter(getNabFilter(), query.getSortOrders(), pageable);
    }

    @Override
    protected int sizeInBackEnd(Query<Nab, NabService.NabFilter> query) {
        return (int) nabService.countByNabFilter(getNabFilter());
    }

    private NabService.NabFilter getNabFilter() {
        return getOptionalFilter().orElse(defaultFilter);
//        return "%" + filter + "%";
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }
}
