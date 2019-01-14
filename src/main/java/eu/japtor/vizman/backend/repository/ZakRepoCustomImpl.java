package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zak;
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
    public Zak[] getNotArchivedArray() {
        Query query = em.createQuery("select zak from Zak zak where zak.arch = false");
        List<Zak> zakList = query.getResultList();
        Zak[] zakArray = new Zak[zakList.size()];
        return zakList.toArray(zakArray);
    }

    @Override
    public int getCountByIdKontAndCzak(Long idKont, Integer czak) {
//        Query query = em.createQuery("select zak from Zak zak where (zak.kont.id=?) and (kont.czak=?)");
//        Query query = em.createQuery("SELECT zak FROM Zak zak WHERE zak.idKont = :deptName");
//        query.setParameter("deptName", dept);

        List<Zak> zaks = em.createNativeQuery(
                "SELECT id  FROM zak z WHERE z.id_kont = :idKont AND z.czak = :czak"
                )
                .setParameter("idKont", idKont)
                .setParameter("czak", czak)
                .getResultList();
        return zaks.size();
    };
}
