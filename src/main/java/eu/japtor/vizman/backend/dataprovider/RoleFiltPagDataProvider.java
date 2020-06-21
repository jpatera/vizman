package eu.japtor.vizman.backend.dataprovider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import eu.japtor.vizman.backend.dataprovider.spring.FilterablePageableDataProvider;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

public class RoleFiltPagDataProvider
                extends FilterablePageableDataProvider<Role, RoleService.RoleFilter>
{
    private final RoleService roleService;

    private final RoleService.RoleFilter defaultFilter = RoleService.RoleFilter.getEmpty();
    private final Role defaultProbe = Role.getEmptyInstance();

    private final List<QuerySortOrder> defaultSortOrders = Arrays.asList(
            new QuerySortOrder("name", SortDirection.ASCENDING)
    );

    public RoleFiltPagDataProvider(RoleService roleService) {
        this.roleService = roleService;
    }

    private static Sort.Order queryOrderToSpringOrder(QuerySortOrder queryOrder) {
        return new Sort.Order(queryOrder.getDirection() == SortDirection.ASCENDING
                ? Sort.Direction.ASC : Sort.Direction.DESC
                , queryOrder.getSorted());
    }

    @Override
    protected Page<Role> fetchFromBackEnd(Query<Role, RoleService.RoleFilter> query, Pageable pageable) {
        return roleService.fetchByRoleFilter(getRoleFilter(), query.getSortOrders(), pageable);
    }

    @Override
    protected int sizeInBackEnd(Query<Role, RoleService.RoleFilter> query) {
        return (int) roleService.countByRoleFilter(getRoleFilter());
    }

    private RoleService.RoleFilter getRoleFilter() {
        return getOptionalFilter().orElse(defaultFilter);
//        return "%" + filter + "%";
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return defaultSortOrders;
    }
}
