package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZakRepo extends JpaRepository<Zak, Long>, ZakRepoCustom {


//    Zak findTopByCkontIgnoreCaseAndCzak(String ckont, Integer czak);

    Zak findTopByTextIgnoreCase(String text);

//    Zak findTopByFolderIgnoreCase(String docdir);

//    List<Zak> findAllByOrderByObjednatel();

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

    List<Zak> findByTextLikeIgnoreCase(String text, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

    int countByTextLikeIgnoreCase(String text);
}
