package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.repository.RoleRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @PersistenceContext
    public EntityManager em;

    @Autowired
    private RoleRepo roleRepo;

//    @Autowired
//    public RoleServiceImpl() {
//    }

    @Override
    public Role fetchRoleByNameIgnoreCase(String name) {
        return roleRepo.findFirstByNameIgnoreCase(name);
    }

    @Override
    public List<Role> fetchAll() {
        return roleRepo.findAllByOrderByName();
    }

    @Override
    public long countAllRoles() {
        return roleRepo.count();
    }


    // Using @Transactional
    @Transactional
    @Override
    public Role fetchByIdEager(Long roleId) {
        Optional<Role> roleResponse = roleRepo.findById(roleId);
        Role role = roleResponse.get();
        Hibernate.initialize(role.getPersons());
        return role;
    }

    @Transactional
    @Override
    public Role fetchByIdLazy(final Long roleId) {
        return roleRepo.getOne(roleId);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepo.saveAndFlush(role);
    }

    @Override
    public void deleteRole(Role role) {
        roleRepo.deleteById(role.getId());
    }


    @Override
    public Page<Role> fetchByRoleFilter(RoleFilter roleFilter, List<QuerySortOrder> sortOrders, Pageable pageable) {
        if (roleFilter == null) {
            return roleRepo.findAll(pageable);
        } else {
            Page<Role> pg = roleRepo.findAll(Example.of(Role.getInstanceFromFilter(roleFilter), getRoleMatcher()), pageable);
            return pg;
//            return nabViewRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public List<Role> fetchFilteredList(RoleFilter roleFilter) {
        if (roleFilter == null) {
            return roleRepo.findAll();
        } else {
            return roleRepo.findAll(Example.of(Role.getInstanceFromFilter(roleFilter)));
        }
    }

    @Override
    public long countByRoleFilter(RoleFilter roleFilter) {
        if (roleFilter == null) {
            return roleRepo.count();
        } else {
            return roleRepo.count(Example.of(Role.getInstanceFromFilter(roleFilter), getRoleMatcher()));
        }
    }

    private ExampleMatcher getRoleMatcher()  {
        return ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("name", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().startsWith())
                ;
    }

}
