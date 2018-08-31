package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Usr;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
@Transactional(readOnly = true)
public class UsrRepoCustomImpl implements UsrRepoCustom  {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Usr getUsrByUsername(final String username) {
        Query query = entityManager.createNativeQuery("SELECT usr.* FROM Usr as usr " +
                "WHERE usr.username = ?", Usr.class);
        query.setParameter(1, username);
//        query.setParameter(1, firstName + "%");
        return (Usr)query.getResultList().get(0);
    }
}
