package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.japtor.vizman.backend.entity.CalTreeNode;
import eu.japtor.vizman.backend.entity.Caly;
import eu.japtor.vizman.backend.entity.Calym;
import eu.japtor.vizman.backend.service.CalService;
import org.springframework.data.domain.*;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CfgCalTreeDataProvider
                extends AbstractBackEndHierarchicalDataProvider<CalTreeNode, CalTreeNode>
//            extends AbstractBackEndHierarchicalDataProvider<CalTreeNode, CalymFilter> {
{
    private final CalService calService;
    private CalTreeNode defaultFilter = null;
    //    private final ToLongFunction<String> lengthFunction;

    private final ExampleMatcher matcher = ExampleMatcher.matching()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreNullValues();

//    private Example<Caly> buildCalyExample(Caly probe) {
//        return Example.of(probe, matcher);
//    }

    private Example<CalTreeNode> buildCalyExample(CalTreeNode probe) {
        return Example.of(probe, matcher);
    }



    public CfgCalTreeDataProvider(CalService calService) {
        this.calService = calService;
    }

    private final List<QuerySortOrder> defaultSortOrders = new ArrayList<>();


    private static Sort.Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Sort.Order(queryOrder.getDirection() == SortDirection.ASCENDING
                ? Sort.Direction.ASC : Sort.Direction.DESC
                , queryOrder.getSorted());
    }


    // From: https://github.com/Artur-/spring-data-provider/blob/master/src/main/java/org/vaadin/artur/spring/dataprovider/PageableDataProvider.java
    private Sort createSpringSort(Query<CalTreeNode, CalTreeNode> query) {
        List<QuerySortOrder> sortOrders;
        if (null == query.getSortOrders()) {
            sortOrders = defaultSortOrders;
        } else {
            sortOrders = query.getSortOrders();
        }

        if (sortOrders.size() == 0) {
            return Sort.unsorted();
        } else {
            List<Sort.Order> orders = sortOrders.stream()
                    .map(CfgCalTreeDataProvider::queryOrderToSpringOrder)
                    .collect(Collectors.toList());
            return Sort.by(orders);

//            SortDirection qSortDirection = sortOrders.get(0).getDirection();
//            String qSortProp = sortOrders.get(0).getSorted();
//            return Sort.by(
//                    qSortDirection == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC
//                    , qSortProp
//            );
        }
    }


    // From: https://github.com/Artur-/spring-data-provider/blob/master/src/main/java/org/vaadin/artur/spring/dataprovider/PageableDataProvider.java
    public static Pair<Integer, Integer> limitAndOffsetToPageSizeAndNumber(
            int offset, int limit) {
        int minPageSize = limit;
        int lastIndex = offset + limit - 1;
        int maxPageSize = lastIndex + 1;

        for (double pageSize = minPageSize; pageSize <= maxPageSize; pageSize++) {
            int startPage = (int) (offset / pageSize);
            int endPage = (int) (lastIndex / pageSize);
            if (startPage == endPage) {
                // It fits on one page, let's go with that
                return Pair.of((int) pageSize, startPage);
            }
        }

        // Should not really get here
        return Pair.of(maxPageSize, 0);
    }

    private Pageable getPageable(Query <CalTreeNode, CalTreeNode> query) {
        Pair<Integer, Integer> pageSizeAndNumber = limitAndOffsetToPageSizeAndNumber(
                query.getOffset(), query.getLimit());
        return PageRequest.of(pageSizeAndNumber.getSecond(), pageSizeAndNumber.getFirst(), createSpringSort(query));
    }


    @Override
//    protected Stream<CalTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<CalTreeNode, CalymFilter> query) {
    protected Stream<CalTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<CalTreeNode, CalTreeNode> hQuery) {

//        hQuery.getSortOrders();

//        List<QuerySortOrder> qSortOrders = hQuery.getSortOrders();

//        List<CfgCalSortOrders> sortOrders = new ArrayList<>();
//        for(SortOrder<String> queryOrder : hQuery.getSortOrders()) {
//            CfgCalSort sort = calService.createSort(
//                    // The name of the sorted property
//                    queryOrder.getSorted(),
//                    // The sort direction for this property
//                    queryOrder.getDirection() == SortDirection.DESCENDING);
//            sortOrders.add(sort);
//        }

        Pageable pageable =  getPageable(hQuery);

////        Optional<CalTreeNode> parentOpt = hQuery.getParentOptional();
//        hQuery.getFilter()
//                .map(probe -> calService.fetchAllCalys(buildCalyExample(probe), ChunkRequest.of(hQuery, defaultSort)).getContent()))
//                .map(probe -> calService.findAll(buildExample(document), ChunkRequest.of(q, defaultSort)).getContent()))
//
        CalTreeNode probe = hQuery.getFilter().orElse(null);


        if (hQuery.getParent() == null) { // Root nodes - no parent by definition
//            List<CalTreeNode> rootNodes = calService.fetchAllCalRootNodes();
            List<Caly> rootNodes = calService.fetchAllCalys(probe, pageable);
//                    hQuery.getOffset()
//                    , hQuery.getLimit()
//                    , sortOrders
//            );
            return rootNodes.stream()
                    .map(cy -> (CalTreeNode) cy)
//                    .skip(hQuery.getOffset())
//                    .limit(hQuery.getLimit())
            ;
        } else if (null != hQuery.getParent().getYr()) {    // Not null year is provided only by Caly
//            List<CalTreeNode> childNodes = calService.fetchCalymNodesByYear(Integer.valueOf(hQuery.getParent().getYr()));
//            return childNodes.stream()
//                    .skip(hQuery.getOffset())
//                    .limit(hQuery.getLimit())
//            ;

            List<Calym> childs = calService.fetchCalymsByYear(
                    Integer.valueOf(hQuery.getParent().getYr())
            );
            return childs.stream()
                    .map(c -> (CalTreeNode) c)
//                    .skip(hQuery.getOffset())
//                    .limit(hQuery.getLimit())
            ;

//
//                return childNodes.stream()
//                        .map(s -> {
//                            return (Caly) s;   // casting to super type for the Stream
//                        }
//                );
        } else {    // Null years have only Calym items
            return null;
        }
    }


    @Override
    public int getChildCount(HierarchicalQuery<CalTreeNode, CalTreeNode> hQuery) {
//    public int getChildCount(HierarchicalQuery<CalTreeNode, CalymFilter> query) {

//        CalTreeNode parent = query.getParentOptional().orElse(null);
        if (null == hQuery.getParent()) {
//            return calService.fetchAllCalRootNodes().size();
//            return calService.fetchAllCalys().size();

            hQuery.getFilter().orElse(defaultFilter);

            return (int)calService.countAllCalys( hQuery.getFilter().orElse(null), getPageable(hQuery));
        } else if (null != hQuery.getParent().getYr()) {
            return calService.fetchCalymNodesByYear(Integer.valueOf(hQuery.getParent().getYr())).size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasChildren(CalTreeNode calNode) {
        return null == calNode || calNode.getYr() != null;
    }
}
