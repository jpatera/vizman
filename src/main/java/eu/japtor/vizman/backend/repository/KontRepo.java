package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Kont;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KontRepo extends JpaRepository<Kont, Long> {

//   Kont findTopByObjednatel(String objednatel);

    Kont findTopByObjednatelIgnoreCase(String objednatel);

    Kont findTopByCkontIgnoreCase(String ckont);

    Kont findTopByTextIgnoreCase(String text);

    Kont findTopByDocdirIgnoreCase(String docdir);

    List<Kont> findAllByOrderByObjednatel();

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

    List<Kont> findByObjednatelLikeIgnoreCase(String username, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

    int countByObjednatelLikeIgnoreCase(String objenatel);

}
