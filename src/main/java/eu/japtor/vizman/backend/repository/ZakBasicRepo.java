package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ZakBasicRepo extends JpaRepository<ZakBasic, Long> {

    List<ZakBasic> findAllByOrderByCkontDescCzakDesc();

//    List<Klient> findByNameLikeIgnoreCase(String username, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

//    int countByNameLikeIgnoreCase(String username);

    @Query("SELECT zb FROM ZakBasic zb WHERE "
            + " (:arch is null or zb.arch = :arch) "
            + " and (:digi is null or zb.digi = :digi) "
            + " and (:ckont is null or zb.ckont like %:ckont%) "
            + " and (:rok is null or zb.rok = :rok) "
            + " and (:skup is null or zb.skupina = :skup) "
            + " order by ckont desc, czak desc ")
    List<ZakBasic> findZakBasicByArchAndDigiAndCkontAndRokAndSkupina(
            @Param("arch") Boolean arch
            , @Param("digi") Boolean digi
            , @Param("ckont") String ckont
            , @Param("rok") Integer rokZak
            , @Param("skup") String skup
    );
}
