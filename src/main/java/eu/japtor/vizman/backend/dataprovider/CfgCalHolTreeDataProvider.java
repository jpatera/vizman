package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.dataprovider.spring.PageableTreeDataProvider;
import eu.japtor.vizman.backend.entity.*;
import eu.japtor.vizman.backend.service.CalService;
import org.springframework.data.domain.*;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CfgCalHolTreeDataProvider
                extends PageableTreeDataProvider<CalHolTreeNode, CalHolTreeNode>
{
    private final CalService calService;

    private final CalHolTreeNode defaultCalyFilter = Caly.getEmptyInstance();
    private final CalHolTreeNode defaultCalyHolFilter = CalyHol.getEmptyInstance();

    private final List<QuerySortOrder> defaultSortOrders = Arrays.asList(
            new QuerySortOrder("yr", SortDirection.DESCENDING)
//            , new QuerySortOrder("ym", SortDirection.ASCENDING)
    );

    private final ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreNullValues();


    public CfgCalHolTreeDataProvider(CalService calService) {
        this.calService = calService;
    }

//    @Override
//    public int getChildCount(HierarchicalQuery<CalTreeNode, Void> hierarchicalQuery) {
//        return 0;
//    }

    @Override
    public boolean hasChildren(final CalHolTreeNode calNode) {
        return null == calNode || ((calNode.getHolDate() == null) && (calService.countCalyHolsByYear(calNode.getYr()) > 0));
    }

    @Override
    public int getChildCount(HierarchicalQuery<CalHolTreeNode, CalHolTreeNode> hQuery) {

//        CalTreeNode parent = query.getParentOptional().orElse(null);
        if (null == hQuery.getParent()) {
            return (int)calService.countCalysByExample(
                    buildCalyExample(hQuery), getPageable(hQuery)
            );
        } else if (null == hQuery.getParent().getHolDate()) {
            return (int)calService.countCalyHolsByExample(
                    buildCalyHolExample(hQuery, hQuery.getParent().getYr()), getPageable(hQuery)
            );
        } else {
            return 0;
        }
    }


    @Override
    protected Page<CalHolTreeNode> fetchChildrenFromBackEnd(
//    protected Page<CalHolTreeNode> fetchChildrenFromBackEnd(
            HierarchicalQuery<CalHolTreeNode, CalHolTreeNode> hQuery, Pageable pageable)
    {
////        Optional<CalTreeNode> parentOpt = hQuery.getParentOptional();
//        hQuery.getFilter()
//                .map(probe -> calService.fetchCalysByExample(buildCalyExample(probe), ChunkRequest.of(hQuery, defaultSort)).getContent()))
//                .map(probe -> calService.findAll(buildExample(document), ChunkRequest.of(q, defaultSort)).getContent()))

        if (hQuery.getParent() == null) { // Only root nodes have null parents
//            Pageable pageable =  PageRequest.of(0, 8, sort);
            // TODO: rewrite to "return fromPageaable(...)":
//            Page<Caly> rootNodes = calService.fetchCalysByExample(
            Page<Caly> rootNodes = calService.fetchCalysByExample(
                    buildCalyExample(hQuery), getPageable(hQuery)
            );
            List<CalHolTreeNode> rootList  = rootNodes.stream()
                    .map(ch -> (CalHolTreeNode) ch)
                    .collect(Collectors.toList());
            ;
            return new PageImpl(rootList, pageable, rootList.size());
//            Page<CalHolTreeNode> pg = new PageImpl(lst, pageable, lst.size());
//            return (Page<CalHolTreeNode>)rootNodes;
//            return rootNodes.stream()
//                    .map(ch -> (CalHolTreeNode) ch)
//            ;
        } else if (null == hQuery.getParent().getHolDate()) {    // Not null years are provided only by Caly (not leaf)
//            List<CalyHol> childs = calService.fetchCalyHolsByExample(
            Example<CalyHol> example = buildCalyHolExample(hQuery, hQuery.getParent().getYr());
            Page<CalyHol> childNodes = calService.fetchCalyHolsByExample(
                    example, getPageable(hQuery)
            );
            List<CalHolTreeNode> childList  = childNodes.stream()
                    .map(ch -> (CalHolTreeNode) ch)
                    .collect(Collectors.toList());
            ;
            return new PageImpl(childList, pageable, childList.size());
//            return childs.stream()
//                    .map(ch -> (CalHolTreeNode) ch)
//            ;
//            return childs.getContent();
        } else {    // Null years are provided by leaf Calym items
            return null;
        }
    }

    private Example<Caly> buildCalyExample(final HierarchicalQuery query) {
        return Example.of((Caly)query.getFilter().orElse(defaultCalyFilter), matcher);
    }

    private Example<CalyHol> buildCalyHolExample(final HierarchicalQuery query, final Integer yr) {
//        CalyHol example = (CalyHol)query.getFilter().orElse(defaultCalyHolFilter);
        CalyHol probe = CalyHol.getEmptyInstance();
        probe.setYr(yr);
        return Example.of(probe, matcher);
    }


    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }

    private static Sort.Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Sort.Order(queryOrder.getDirection() == SortDirection.ASCENDING
                ? Sort.Direction.ASC : Sort.Direction.DESC
                , queryOrder.getSorted());
    }

    // From: https://github.com/Artur-/spring-data-provider/blob/master/src/main/java/org/vaadin/artur/spring/dataprovider/PageableDataProvider.java
    private Sort createSpringSort(Query<CalTreeNode, CalTreeNode> query) {
        List<QuerySortOrder> sortOrders;
        if (CollectionUtils.isEmpty(query.getSortOrders())) {    // Sort orders is never null (by Vaadin), empty  list is default
            sortOrders = defaultSortOrders;
        } else {
            sortOrders = query.getSortOrders();
        }
        if (sortOrders.size() == 0) {
            return Sort.unsorted();
        } else {
            List<Sort.Order> orders = sortOrders.stream()
                    .map(CfgCalHolTreeDataProvider::queryOrderToSpringOrder)
                    .collect(Collectors.toList());
            return Sort.by(orders);
        }
    }

//    // From: https://github.com/Artur-/spring-data-provider/blob/master/src/main/java/org/vaadin/artur/spring/dataprovider/PageableDataProvider.java
//    private static Pair<Integer, Integer> limitAndOffsetToPageSizeAndNumber(
//            int offset, int limit) {
//        int minPageSize = limit;
//        int lastIndex = offset + limit - 1;
//        int maxPageSize = lastIndex + 1;
//
//        for (double pageSize = minPageSize; pageSize <= maxPageSize; pageSize++) {
//            int startPage = (int) (offset / pageSize);
//            int endPage = (int) (lastIndex / pageSize);
//            if (startPage == endPage) {
//                // It fits on one page, let's go with that
//                return Pair.of((int) pageSize, startPage);
//            }
//        }
//
//        // Should not really get here
//        return Pair.of(maxPageSize, 0);
//    }
//
//    private Pageable getPageable(Query <CalTreeNode, CalTreeNode> query)
//    {
//        Pair<Integer, Integer> pageSizeAndNumber = limitAndOffsetToPageSizeAndNumber(
//                query.getOffset(), query.getLimit());
//        return PageRequest.of(pageSizeAndNumber.getSecond(), pageSizeAndNumber.getFirst(), createSpringSort(query));
//    }
//
//    private <T> Stream<T> fromPageable(Page<T> result, Pageable pageable, Query<T, ?> query)
//    {
//        List<T> items = result.getContent();
//        int firstRequested = query.getOffset();
//        int nrRequested = query.getLimit();
//        int firstReturned = (int) pageable.getOffset();
//        int firstReal = firstRequested - firstReturned;
//        int afterLastReal = firstReal + nrRequested;
//        if (afterLastReal > items.size()) {
//            afterLastReal = items.size();
//        }
//        return items.subList(firstReal, afterLastReal).stream();
//    }
}
