package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.ZakBasic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ZakBasicRepo extends JpaRepository<ZakBasic, Long> {

    List<ZakBasic> findAllByOrderByCkontDescCzakDesc();
//    List<ZakBasic> findAll();

//    List<Klient> findByNameLikeIgnoreCase(String username, Sort sort);
    // TODO: more versatile might be using Example matchers
    // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
    // See: https://vaadin.com/forum/thread/16031323
    // findAll(Example.of(userProbe, userMatcher));

//    int countByNameLikeIgnoreCase(String username);

}
