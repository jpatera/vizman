package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Role;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {

//    Role fetchRoleById(Long id);

    Role fetchRoleByNameIgnoreCase(String name);

    Role fetchByIdWithLazyPersons(Long roleId);

    Role saveRole(Role role);

    void deleteRole(Role role);

    List<Role> fetchAll();

    long countAllRoles();

    Page<Role> fetchByRoleFilter(RoleFilter roleFilter, List<QuerySortOrder> sortOrders, Pageable pageable);
    List<Role> fetchFilteredList(RoleFilter roleFilter);
    long countByRoleFilter(RoleFilter roleFilter);

    class RoleFilter {
        String name;
        String description;

        public RoleFilter(
                String name
                , String description
        ) {
            this.name = name;
            this.description = description;
        }

        public static final RoleFilter getEmpty() {
            return new RoleFilter(null, null);
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
