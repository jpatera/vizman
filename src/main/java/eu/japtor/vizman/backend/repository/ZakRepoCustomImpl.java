package eu.japtor.vizman.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ZakRepoCustomImpl implements ZakRepoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public String[] getAllFirmaArray() {
        Query query = em.createQuery("select distinct zak.firma from Zak zak");
        List<String> firmaList = query.getResultList();
        String[] firmaArray = new String[firmaList.size()];
        return firmaList.toArray(firmaArray);
    }
}
