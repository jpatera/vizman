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


// INFO: Following query returns  for more than one zak_id a correct number of records, but  all
//       contain only one of the submitted zak_id values. Probably a H2 & Hibernate bug.
//  SEE: Alternative  method in  eu.japtor.vizman.backend.repository.ZaknRepoCustomImpl.findByZakIdsSumByYm
//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id in (:ids) ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//        , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(@Param("ids") List<Long> zakIds);

//    @Query(value = "SELECT * FROM vizman.ZAK_YM_NAKL_VIEW WHERE zak_id=4026618 or zak_id=4026617 or zak_id=4026616 ORDER BY zak_id DESC, person_id ASC, ym_pruh ASC"
//        , nativeQuery = true)
//    List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds);



    @Autowired(required = true)
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    PersonRepo personRepo;

    @Override
    public List<ZakYmNaklVw> findByZakIdsSumByYm(List<Long> zakIds) {
//        List<ZakYmNaklVw> lst = this.jdbcTemplate.queryForList("SELECT * from  vizman.zak_ym_nakl_view", ZakYmNaklVw.class);
        SqlParameterSource params = new MapSqlParameterSource("ids", zakIds);
        List<ZakYmNaklVw> lst = this.jdbcTemplate.query(
                "SELECT * from vizman.zak_ym_nakl_view WHERE zak_id in (:ids) ORDER BY ckont DESC, czak DESC, person_id ASC, ym_pruh DESC"
                , params
                , new ZakYmNaklVwRowMapper()
        );

        for (ZakYmNaklVw zakn : lst) {
            zakn.setPerson(personRepo.findTopById(zakn.getPersonIdTrans()));
        }
        return lst;
    }
}