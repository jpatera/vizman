package eu.japtor.vizman.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class KontRepoCustomImpl implements KontRepoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public String[] getAllObjednatelArray() {
        Query query = em.createQuery("select distinct kont.objednatel from Kont kont");
        List<String> objednatelList = query.getResultList();
        String[] objednatelArray = new String[objednatelList.size()];
        return objednatelList.toArray(objednatelArray);
    }
}
