package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class RoleRepoCustomImpl implements RoleRepoCustom  {

    @PersistenceContext
    EntityManager em;

    @Override
    public String[] getAllRoleNames() {
        Query query = em.createQuery("select r from Role r");
        List<String> repoNameList = query.getResultList();
        String[] repoNameArray = new String[repoNameList.size()];
        return repoNameList.toArray(repoNameArray);

//        List<String> list = query.getResultList();
//                "WHERE role.name = ?", Role.class);
//        findAll().stream().map(r -> r.getName()).collect(Collectors.toList()
//        return new String[0];
    }


//    @Override
//    public Role findByName(final String name) {
//        Query query = entityManager.createNativeQuery("SELECT role.* FROM Role as role " +
//                "WHERE role.name = ?", Role.class);
//        query.setParameter(1, name);
////        query.setParameter(1, firstName + "%");
//        List<Role> roles = (List<Role>) query.getResultList();
//        if (roles.isEmpty()) {
//            return null;
//        } else {
//            return roles.get(0);
//        }
//    }
}
