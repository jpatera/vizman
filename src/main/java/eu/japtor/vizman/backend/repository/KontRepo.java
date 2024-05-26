package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Kont;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KontRepo extends JpaRepository<Kont, Long>, KontRepoCustom {

    Kont findTopById(Long id);

    Kont findTopByCkontIgnoreCase(String ckont);

    Kont findTopByTextIgnoreCase(String text);

    Kont findTopByFolderIgnoreCase(String docdir);

    List<Kont> findAllByOrderByCkontDescRokDesc();

    List<Kont> findAllByRokOrderByCkontDesc(Integer rok);

    List<Kont> findTop10ByRokOrderByCkontDesc(Integer rok);

    @Query(value = "SELECT * FROM vizman.kont WHERE ckont LIKE :ckont% ORDER BY ckont DESC limit 10",
            nativeQuery = true)
    List<Kont> findTop10LikeCkontOrderByCkontDesc(@Param("ckont") String ckont);

    List<Kont> findTop1ByCkont(String ckont);

    List<Kont> findTop10ByOrderByCkontDesc();

    @Query(value = "SELECT * FROM vizman.kont knt, vizman.klient kli WHERE kli.name LIKE :objName% "
            + " AND kli.id = knt.id_objednatel ORDER BY ckont DESC limit 10",
            nativeQuery = true)
    List<Kont> findTop10LikeObjNameOrderByCkontDesc(@Param("objName") String objName);

    @Query(value = "SELECT * FROM vizman.kont knt, vizman.klient kli WHERE kli.name LIKE :objName% "
            + " AND kli.id = knt.id_objednatel ORDER BY ckont DESC",
            nativeQuery = true)
    List<Kont> findAllLikeObjNameOrderByCkontDesc(@Param("objName") String objName);


//    List<Kont> findTop10By(Example example);  For report, but does not not work with example

    int countAllByObjednatel(Klient objednatel);

    @Query(value = "SELECT distinct rok FROM vizman.kont ORDER BY ROK DESC",
            nativeQuery = true)
    List<Integer> findKontRoks();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont AND z.arch = false)) "
            + " AND (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont)) "
            + " ORDER BY CKONT DESC, ROK DESC",
                    nativeQuery = true)
    public List<Kont> findHavingAllZaksArchived();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont AND z.arch = false)) "
            + " AND (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont)) "
            + " ORDER BY CKONT DESC, ROK DESC limit 10",
                    nativeQuery = true)
    public List<Kont> findTop10HavingAllZaksArchived();

    @Query(value = "SELECT * FROM vizman.kont k WHERE NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont) "
            + " ORDER BY CKONT DESC, ROK DESC",
                    nativeQuery = true)
    public List<Kont> findHavingNoZaks();

    @Query(value = "SELECT * FROM vizman.kont k WHERE NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont) "
            + " ORDER BY CKONT DESC, ROK DESC limit 10",
                    nativeQuery = true)
    public List<Kont> findTop10HavingNoZaks();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont AND z.arch = false)) "
            + " OR (NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont)) "
            + " ORDER BY CKONT DESC, ROK DESC",
                    nativeQuery = true)
    public List<Kont> findHavingSomeZaksActive();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont AND z.arch = false)) "
            + " OR (NOT EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont)) "
            + " ORDER BY CKONT DESC, ROK DESC limit 10",
            nativeQuery = true)
    public List<Kont> findTop10HavingSomeZaksActive();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont"
            + " AND (exists (SELECT  1 from vizman.fakt f WHERE z.id =  f.id_zak"
            + " AND f.date_vystav is null AND f.date_duzp is not null"
            + " AND f.date_duzp > TODAY()))))"
            + " ORDER BY CKONT DESC, ROK DESC",
                    nativeQuery = true)
    public List<Kont> findHavingSomeZakAvizosGreen();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont"
            + " AND (exists (SELECT  1 from vizman.fakt f WHERE z.id =  f.id_zak"
            + " AND f.date_vystav is null AND f.date_duzp is not null"
            + " AND f.date_duzp > TODAY()))))"
            + " ORDER BY CKONT DESC, ROK DESC limit 10",
                    nativeQuery = true)
    public List<Kont> findTop10HavingSomeZakAvizosGreen();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont"
            + " AND (exists (SELECT  1 from vizman.fakt f WHERE z.id =  f.id_zak"
            + " AND f.date_vystav is null AND f.date_duzp is not null"
            + " AND f.date_duzp + 1 < TODAY()))))"
            + " ORDER BY CKONT DESC, ROK DESC",
                    nativeQuery = true)
    public List<Kont> findHavingSomeZakAvizosRed();

    @Query(value = "SELECT * FROM vizman.kont k WHERE (EXISTS (SELECT 1 FROM vizman.zak z WHERE k.id = z.id_kont"
            + " AND (exists (SELECT  1 from vizman.fakt f WHERE z.id =  f.id_zak"
            + " AND f.date_vystav is null AND f.date_duzp is not null"
            + " AND f.date_duzp + 1 < TODAY()))))"
            + " ORDER BY CKONT DESC, ROK DESC limit 10",
                    nativeQuery = true)
    public List<Kont> findTop10HavingSomeZakAvizosRed();


//    List<Kont> findByObjednatelLikeIgnoreCase(String objednatel, Sort sort);
//    // TODO: more versatile might be using Example matchers
//    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
//    // See: https://vaadin.com/forum/thread/16031323
//    // findAll(Example.of(userProbe, userMatcher));

//    int countByObjednatelLikeIgnoreCase(String objenatel);




//    static class KontSpecs {
//
//        public static Specification<Kont> hasAnyZakArchived() {
//
//            findAll()
//
//            return new Specification<Kont>() {
//                public javax.persistence.criteria.Predicate toPredicate(Root<Kont> root, CriteriaQuery<?> query,
//                                                                        CriteriaBuilder builder) {
//
////                    root.getArch()
////                    LocalDate date = new LocalDate().minusYears(2);
////                    return builder.isTrue(root.get()  (Kont.getArch());
//                    return builder.isTrue(Boolean.TRUE);
//                }
//            };
//        }
//
//        public static Specification<Kont> hasAnyZakArchived2() {
////        public static Specification<Kont> hasAnyZakArchived(MontaryAmount value) {
//            return new Specification<Kont>() {
//                public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
//                                             CriteriaBuilder builder) {
//
//                    return builder.lessThan(root.get(Kont.createdAt), date);
//                }
//            };
//        }
//    }

}
