package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakYmNaklVw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ZakYmNaklVwRepoCustomImpl implements ZakYmNaklVwRepoCustom {

    @PersistenceContext
    EntityManager em;

//    @Override
//    public List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds) {
////        String sql = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (:ids) ORDER BY ckont DESC, czak ASC, person_id ASC, ym_pruh DESC";
////        String sql = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (4026618, 4026617, 4026616) ORDER BY ckont DESC, czak ASC, person_id ASC, ym_pruh DESC";
////        String sql = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (4026618, 4026617, 4026616)";
//
////        String sql = "SELECT z FROM ZakYmNaklVw z WHERE zakId in (4026618, 4026617, 4026616)";
//
//        String cond = String.join(" or ", zakIds.stream()
//                .map (id -> "zak_id=" + id.toString())
//                .collect(Collectors.toList())
////                .reduce("", String::concat)
//        );
//        String sql = "SELECT * FROM vizman.zak_ym_nakl_view WHERE " + cond;
////        String cond = "zak_id = 4026618 or zak_id = 4026617 or zak_id = 4026616)";
////        List<ZakYmNaklVw> zakns = em.createNativeQuery(sql, ZakYmNaklVw.class).getResultList();
//        List<ZakYmNaklVw> zakns = em.createNativeQuery(sql).getResultList();
////        List<Object> zakns = em.createNativeQuery(sql).getResultList();
//
////        TypedQuery<ZakYmNaklVw> tq = em.createQuery(s, ZakYmNaklVw.class);
////        List<Object> zakns = tq.getResultList();
//
////        List<ZakYmNaklVw> zakns = em.createNativeQuery(sql)
////            .setParameter("ids", zakIds)
////            .getResultList();
//
////        return zakns;
//
////        return (List<ZaknNaklVw>)(Object)zakns;
//
//        return zakns.stream()
//                .map(element->(ZakYmNaklVw) element)
//                .collect(Collectors.toList()
//        );
//    }

// INFO: Following query returns  for more than one zak_id a correct number of records, but  all
//       contein only one of the submitted zak_id values. Probbably som H2 & Hibernate bug.
//  SEE: Alternative  method in  eu.japtor.vizman.backend.repository.ZaknRepoCustomImpl.findByZakIdsSumByYm
//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (:ids) ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//        , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(@Param("ids") List<Long> zakIds);

//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id=4026618 or zak_id=4026617 or zak_id=4026616 ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//        , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds);



    @Autowired(required = true)
//    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    PersonRepo personRepo;

    @Override
    public List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds) {
//        List<ZakYmNaklVw> lst = this.jdbcTemplate.queryForList("SELECT * from  vizman.zak_ym_nakl_view", ZakYmNaklVw.class);
        SqlParameterSource params = new MapSqlParameterSource("ids", zakIds);
        List<ZakYmNaklVw> lst = this.jdbcTemplate.query(
                "SELECT * from vizman.zak_ym_nakl_view WHERE zak_id in (:ids) ORDER BY zak_id DESC, person_id ASC, ym_pruh DESC"
                , params
                , new ZakYmNaklVwRowMapper()
        );

        for (ZakYmNaklVw zakn : lst) {
            zakn.setPerson(personRepo.findTopById(zakn.getPersonIdTrans()));
        }
        return lst;
    }

}