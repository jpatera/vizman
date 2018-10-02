package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Role;
import eu.japtor.vizman.backend.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepo roleRepo;

    @Autowired
    public RoleServiceImpl(RoleRepo roleRepo) {
        super();
        this.roleRepo = roleRepo;
    }

    @Override
    public Role fetchRoleByName(String name) {
        return null;
    }

    @Override
    public List<Role> fetchAllRoles() {
        return roleRepo.findAllByOrderByName();
    }

    @Override
    public long countAllRoles() {
        return roleRepo.count();
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepo.saveAndFlush(role);
    }

    @Override
    public void deleteRole(Role role) {
        roleRepo.deleteById(role.getId());
    }
}
