package eu.japtor.vizman.backend.dataprovider.spring;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PageableTreeDataProvider<T, F>
        extends AbstractBackEndHierarchicalDataProvider<T, F> {

//    @Override
//    public boolean hasChildren(final T node) {
//        return null == node || node.getYr() != null;
//    }

//    @Override
//    public int getChildCount(HierarchicalQuery<T, F> hQuery) {
//
////        CalTreeNode parent = query.getParentOptional().orElse(null);
//        if (null == hQuery.getParent()) {
//            return (int)calService.countCalysByExample(buildCalyExample(hQuery), getPageable(hQuery));
//        } else if (null != hQuery.getParent().getYr()) {
//            return (int)calService.countCalymsByYear(hQuery.getParent().getYr());
//        } else {
//            return 0;
//        }
//    }

    @Override
    protected Stream<T> fetchChildrenFromBackEnd(HierarchicalQuery<T, F> hQuery) {
        Pageable pageable = getPageable(hQuery);
        Page<T> result = fetchChildrenFromBackEnd(hQuery, pageable);
//        List<T> result = fetchChildrenFromBackEnd(hQuery, pageable);
//        return fromPageable(result, pageable, hQuery);
        return result.stream();
    }

    protected abstract Page<T> fetchChildrenFromBackEnd(HierarchicalQuery<T, F> query, Pageable pageable);

    protected Pageable getPageable(HierarchicalQuery<T, F> q) {
        Pair<Integer, Integer> pageSizeAndNumber = limitAndOffsetToPageSizeAndNumber(
                q.getOffset(), q.getLimit());
        return PageRequest.of(pageSizeAndNumber.getSecond(), pageSizeAndNumber.getFirst(), createSpringSort(q));
    }

    private <T, F> Sort createSpringSort(HierarchicalQuery<T, F> q) {
        List<QuerySortOrder> sortOrders;
        if (q.getSortOrders().isEmpty()) {
            sortOrders = getDefaultSortOrders();
        } else {
            sortOrders = q.getSortOrders();
        }
        List<Order> orders = sortOrders.stream()
                .map(PageableTreeDataProvider::queryOrderToSpringOrder)
                .collect(Collectors.toList());
        if (orders.isEmpty()) {
            return Sort.unsorted();
        } else {
            return Sort.by(orders);
        }
    }

    protected abstract List<QuerySortOrder> getDefaultSortOrders();

    private static Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Order(queryOrder.getDirection() == SortDirection.ASCENDING
                ? Direction.ASC
                : Direction.DESC, queryOrder.getSorted());
    }

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

    private <T> Stream<T> fromPageable(Page<T> result, Pageable pageable, HierarchicalQuery<T, ?> query) {
        List<T> items = result.getContent();

        int firstRequested = query.getOffset();
        int nrRequested = query.getLimit();
        int firstReturned = (int) pageable.getOffset();
        int firstReal = firstRequested - firstReturned;
        int afterLastReal = firstReal + nrRequested;
        if (afterLastReal > items.size()) {
            afterLastReal = items.size();
        }
        return items.subList(firstReal, afterLastReal).stream();
    }
}
