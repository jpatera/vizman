package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
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

//    @Override
//    @Query("SELECT * FROM kont k WHERE EXISTS (SELECT 1 FROM zak z WHERE k.id = z.id_kont AND z.arch = true)")
//    public List<Kont> findHavingAllZaksArchived() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Kont> cqry = cb.createQuery(Kont.class);
//
//        Root<Kont> root = cqry.from(Kont.class);
//        cqry.select(root);
//        Predicate pKontArchived = cb.equal(root.get("objednatel"),"Building");
//        cqry.where(pKontArchived);
//
//        Query qry = em.createQuery(cqry);
//        List<Kont> results = qry.getResultList();
//
//        return results;
////        Join<Kont, Zak> join = root.join(Kont_.zaks);
////        cqry.where(cb.equal(join.get(Zak_.arch), true));
//    }

//    @Override
//    public List<Kont> findHavingSomeZaksActive() {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Kont> cqry = cb.createQuery(Kont.class);
//
//        Root<Kont> root = cqry.from(Kont.class);
//        Join<Kont, List<Zak>> join = root.join("zaks");
//
////        Join<MyEntity,AnotherEntity> join =
////                root.join(MyEntity_.anotherEntity); //Step 2
////        //Join<MyEntity,AnotherEntity> join =
////        root.join("anotherEntity"); //Step 2
//
//        cqry.select(root);
//        Predicate pKontActive = cb.equal(root.get("objednatel"),"AŽD Praha");
//        cqry.where(pKontActive);
//
//        Query qry = em.createQuery(cqry);
//        List<Kont> results = qry.getResultList();
//
//        return results;
////        Join<Kont, Zak> join = root.join(Kont_.zaks);
////        cqry.where(cb.equal(join.get(Zak_.arch), true));
//    }

}
