package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepo extends JpaRepository<Role, Long>, RoleRepoCustom {

    Role findTopByName(String name);
    List<Role> findAll();
}
