package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.entity.Zakr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ZakrRepo extends JpaRepository<Zakr, Long> {

    List<Zakr> findAllByOrderByCkontDescCzakDesc();

    List<Zakr> findByRokOrderByCkontDescCzakDesc(Integer rok);


    @Query("SELECT zr AS AA FROM Zakr zr WHERE "
            + " (:active = false  or (zr.rp = null or zr.rp <> 100 or ((zr.r0 <> null or zr.r1 <> null or zr.r2 <> null or zr.r3 <> null or zr.r4 <> null) and (zr.r0 <> 100 or zr.r1 <> 100 or zr.r2 <> 100 or zr.r3 <> 100 or zr.r4 <> 100)))) "
            + " order by zr.ckont desc, zr.czak desc "
    )
    List<Zakr> findZakrByActiveFilterOrderByCkontDescCzakDesc(
            @Param("active") Boolean active
    );


    @Query("SELECT zr AS AA FROM Zakr zr WHERE "
            + " (:arch is null or zr.arch = :arch) "
            + " and (:ckont is null or zr.ckont like %:ckont%) "
            + " and (:rok is null or zr.rok = :rok) "
            + " and (:skup is null or zr.skupina = :skup) "
            + " and (:active = false  or (zr.rp = null or zr.rp <> 100 or ((zr.r0 <> null or zr.r1 <> null or zr.r2 <> null or zr.r3 <> null or zr.r4 <> null) and (zr.r0 <> 100 or zr.r1 <> 100 or zr.r2 <> 100 or zr.r3 <> 100 or zr.r4 <> 100)))) "
            + " order by zr.ckont desc, zr.czak desc "
    )
    List<Zakr> findZakrByFilterParams(
            @Param("arch") Boolean arch
            , @Param("ckont") String ckont
            , @Param("rok") Integer rokZak
            , @Param("skup") String skup
            , @Param("active") Boolean active
    );


//    @Query("SELECT id FROM Zakr zr WHERE "
//            + " (:arch is null or zr.arch = :arch) "
//            + " and (:ckont is null or zr.ckont like %:ckont%) "
//            + " and (:rok is null or zr.rok = :rok) "
//            + " and (:skup is null or zr.skupina = :skup) "
//            + " and (:active = false  or (zr.rp = null or zr.rp <> 100 or ((zr.r0 <> null or zr.r1 <> null or zr.r2 <> null or zr.r3 <> null or zr.r4 <> null) and (zr.r0 <> 100 or zr.r1 <> 100 or zr.r2 <> 100 or zr.r3 <> 100 or zr.r4 <> 100)))) "
//            + " order by zr.ckont desc, zr.czak desc "
//    )
    @Query(value = "SELECT id FROM vizman.zak_rozprac_view zr WHERE "
            + " (:arch is null or zr.arch = :arch) "
            + " and (:ckont is null or zr.ckont like %:ckont%) "
            + " and (:rok is null or zr.rok = :rok) "
            + " and (:skup is null or zr.skupina = :skup) "
            + " and (:active = false  or (zr.rp is null or zr.rp <> 100 or ((zr.r0 is not null or zr.r1 is not null or zr.r2 is not null or zr.r3 is not null or zr.r4 is not null) and (zr.r0 <> 100 or zr.r1 <> 100 or zr.r2 <> 100 or zr.r3 <> 100 or zr.r4 <> 100)))) "
            + " order by zr.ckont desc, zr.czak desc "
            + " limit 20 "
            , nativeQuery = true
    )
    List<Long> findIdsByFilterParamsWithLimit(
            @Param("arch") Boolean arch
            , @Param("ckont") String ckont
            , @Param("rok") Integer rokZak
            , @Param("skup") String skup
            , @Param("active") Boolean active
    );

    @Query(value = "SELECT distinct rok FROM vizman.zak_rozprac_view ORDER BY ROK DESC",
            nativeQuery = true)
    List<Integer> findZakrRoks();

    Zakr findTopById(Long id);

}
