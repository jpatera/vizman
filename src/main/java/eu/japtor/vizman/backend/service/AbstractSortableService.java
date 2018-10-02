package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbstractSortableService {

    abstract List<QuerySortOrder> getDefaultSortOrders();

    protected Sort mapSortOrdersToSpring(List<QuerySortOrder> sortOrders) {
        if (sortOrders == null || sortOrders.isEmpty()) {
            return mapSortOrders(getDefaultSortOrders());
        } else {
            return mapSortOrders(sortOrders);
        }
    }

    private Sort mapSortOrders(List<QuerySortOrder> sortOrders) {
        return Sort.by(sortOrders.stream()
                .map(s -> new Sort.Order(s.getDirection() == SortDirection.ASCENDING
                        ? Sort.Direction.ASC : Sort.Direction.DESC, s.getSorted()))
                .toArray(Sort.Order[]::new));
    }
}
