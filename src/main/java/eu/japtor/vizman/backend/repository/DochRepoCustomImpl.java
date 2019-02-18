package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Doch;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class DochRepoCustomImpl implements DochRepoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<Doch> getPrevDochRecords(final Long personId, final LocalDate beforeDochDate) {
        // TODO: this is only a fake, fix it
        Query query = em.createQuery("SELECT d FROM Doch d where d.personId=2");
//        Query query = em.createQuery("SELECT d FROM Doch d");
        List<Doch> prevDochRecs = query.getResultList();
        return prevDochRecs;
    }
}
