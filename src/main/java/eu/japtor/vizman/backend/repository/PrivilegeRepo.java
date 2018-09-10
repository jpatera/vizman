package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

//public interface PrivilegeRepo extends JpaRepository<Privilege, Long>, PrivilegeRepoCustom {
public interface PrivilegeRepo {

    Privilege findTopByName(String name);
}