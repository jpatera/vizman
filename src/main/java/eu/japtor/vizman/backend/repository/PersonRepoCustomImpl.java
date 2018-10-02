package eu.japtor.vizman.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class PersonRepoCustomImpl implements PersonRepoCustom {

//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Override
//    public Person getByUsername(final String username) {
//        Query query = entityManager.createNativeQuery("SELECT person.* FROM Person as person " +
//                "WHERE person.username = ?", Person.class);
//        query.setParameter(1, username);
////        query.setParameter(1, firstName + "%");
//        return (Person)query.getResultList().get(0);
//    }
}
