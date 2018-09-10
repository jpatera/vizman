package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Privilege;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

//@Repository
public class PrivilegeRepoCustomImpl implements PrivilegeRepoCustom {

//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Override
//    public Privilege findByName(final String name) {
//
//        Query query = entityManager.createNativeQuery("SELECT role.* FROM Privilege as priv " +
//                "WHERE priv.name = ?", Privilege.class);
//        query.setParameter(1, name);
////        query.setParameter(1, firstName + "%");
//        List<Privilege> privileges = (List<Privilege>) query.getResultList();
//        // ... = query.getSingleResult();
//        if (privileges.isEmpty()) {
//            return null;
//        } else {
//            return privileges.get(0);
//        }
//    }
}
