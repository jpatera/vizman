package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Kont;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KontRepo extends JpaRepository<Kont, Long> {

//   Kont findTopByFirma(String firma);

   Kont findTopByFirmaIgnoreCase(String firma);

    List<Kont> findAllByOrderByFirma();

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

    List<Kont> findByFirmaLikeIgnoreCase(String username, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

    int countByFirmaLikeIgnoreCase(String firma);

}
