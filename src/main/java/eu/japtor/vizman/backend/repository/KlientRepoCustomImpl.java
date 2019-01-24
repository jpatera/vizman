package eu.japtor.vizman.backend.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class KlientRepoCustomImpl implements KlientRepoCustom  {

    @PersistenceContext
    EntityManager em;

    @Override
    public String[] getAllKlientNames() {
        Query query = em.createQuery("select k from Klient k");
        List<String> klientNameList = query.getResultList();
        String[] repoNameArray = new String[klientNameList.size()];
        return klientNameList.toArray(repoNameArray);
    }
}
