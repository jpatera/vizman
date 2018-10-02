package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Role;

import java.util.List;

public interface RoleService {

//    Role fetchRoleById(Long id);

    Role fetchRoleByName(String name);

    Role saveRole(Role role);

    void deleteRole(Role role);

    List<Role> fetchAllRoles();

    long countAllRoles();
//
//    List<Role> fetchRolesByFilter(String filter, List<QuerySortOrder> sortOrders);
//
//    long countRolesByFilter(String filter);
}
