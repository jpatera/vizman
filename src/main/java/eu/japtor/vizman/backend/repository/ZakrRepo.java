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

    @Query("SELECT zr AS AA FROM Zakr zr WHERE (:arch is null or zr.arch = :arch) "
            + " and (:rok is null or zr.rok = :rok) "
            + " and (:skup is null or zr.skupina = :skup) ")
    List<Zakr> findZakrByArchAndRokAndSkupina(@Param("arch") Boolean arch, @Param("rok") Integer rokZak, @Param("skup") String skup);

    @Query(value = "SELECT distinct rok FROM vizman.zak_rozprac_view ORDER BY ROK DESC",
            nativeQuery = true)
    List<Integer> findZakrRoks();

    Zakr findTopById(Long id);

}
