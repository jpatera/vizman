package eu.japtor.vizman.backend.dataprovider.spring;

import com.vaadin.flow.data.provider.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

public class SpringTreeDataProviderBuilder<T, F> {
    private final BiFunction<Pageable, F, Page<T>> queryFunction;
    private final ToLongFunction<F> lengthFunction;
    private final List<QuerySortOrder> defaultSortOrders = new ArrayList<>();

    private F defaultFilter = null;

    public SpringTreeDataProviderBuilder(
            BiFunction<Pageable, F, Page<T>> queryFunction,
            ToLongFunction<F> lengthFunction) {
        this.queryFunction = queryFunction;
        this.lengthFunction = lengthFunction;
    }

    public SpringTreeDataProviderBuilder<T, F> withDefaultSort(String column,
                                                               SortDirection direction) {
        defaultSortOrders.add(new QuerySortOrder(column, direction));
        return this;
    }

    public SpringTreeDataProviderBuilder<T, F> withDefaultFilter(F defaultFilter) {
        this.defaultFilter = defaultFilter;
        return this;
    }

    public DataProvider<T, F> build() {
        return new PageableDataProvider<T, F>() {
            @Override
            protected Page<T> fetchFromBackEnd(Query<T, F> query,
                                               Pageable pageable) {
                return queryFunction.apply(pageable,
                        query.getFilter().orElse(defaultFilter));
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders() {
                return defaultSortOrders;
            }

            @Override
            protected int sizeInBackEnd(Query<T, F> query) {
                return (int) lengthFunction
                        .applyAsLong(query.getFilter().orElse(defaultFilter));
            }
        };
    }

    public ConfigurableFilterDataProvider<T, Void, F> buildFilterable() {
        return build().withConfigurableFilter();
    }

    public static <T> SpringTreeDataProviderBuilder<T, Void> forRepository(
            PagingAndSortingRepository<T, ?> repository) {
        return new SpringTreeDataProviderBuilder<>(
                (pageable, filter) -> repository.findAll(pageable),
                filter -> repository.count());
    }

    public static <T, F> SpringTreeDataProviderBuilder<T, F> forFunctions(
            BiFunction<Pageable, F, Page<T>> queryFunction,
            ToLongFunction<F> lengthFunction) {
        return new SpringTreeDataProviderBuilder<>(queryFunction, lengthFunction);
    }
}
