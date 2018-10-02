package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface RoleRepo extends JpaRepository<Role, Long>, RoleRepoCustom {

    Role findTopByName(String name);

    List<Role> findAllByOrderByName();
}
