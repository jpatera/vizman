package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ItemType;
import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZakRepo extends JpaRepository<Zak, Long>, ZakRepoCustom {


//    List<Zak> findAllOrderByRokDescByDateCreateDesc();
//    List<Zak> findByRokOrderByRokDescByDateCreateDesc(Integer rok);

    List<Zak> findAllByOrderByDateCreateDesc();
    List<Zak> findByRokOrderByRokDescIdDesc(Integer rok);

//    Zak findTopByCkontIgnoreCaseAndCzak(String ckont, Integer czak);

    Zak findTopById(Long id);

    Zak findTopByTextIgnoreCase(String text);

    Zak findTop1ByTyp(ItemType itemType);

//    Zak findTopByFolderIgnoreCase(String docdir);

//    List<Zak> findAllByOrderByObjednatel();

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

    @Query(value = "SELECT distinct rok FROM vizman.zak ORDER BY ROK DESC",
            nativeQuery = true)
    List<Integer> findZakRoks();

    List<Zak> findByTextLikeIgnoreCase(String text, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

    Long countByTextLikeIgnoreCase(String text);

//    Long countByKontIdAndCzak(Long idKont, Integer czak);
    Long countByKont(Long idKont);
    Long countByCzak(Integer czak);
}
