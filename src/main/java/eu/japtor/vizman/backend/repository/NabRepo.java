package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Nab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NabRepo extends JpaRepository<Nab, Long> {

    // TODO: An alternative is to use getOne(Long id) which does not need to be explicitely specified in  repo.
    //       Don't know which one is better. May be findTopById loads sub-items automatically?
    Nab findTopById(Long id);

    Nab findTopByCnab(String cnab);
    List<Nab> findAllByOrderByCnabDescTextAsc();

//    List<Nab> findByTextLikeIgnoreCase(String text, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

//    int countByTextLikeIgnoreCase(String text);

    @Query("SELECT nab FROM Nab nab WHERE "
            + " (:rok is null or nab.rok = :rok) "
            + " and (:text is null or nab.text = :text) "
    )
    List<Nab> findNabByRokAndText(
            @Param("rok") Integer rok
            , @Param("text") String text
    );
}
