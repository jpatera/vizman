package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.PersonRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface PersonRoleRepo extends JpaRepository<PersonRole, Long>, QueryByExampleExecutor<PersonRole> {

        int countByRoleId(Long roleId);

}
