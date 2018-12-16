package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PersonRepo extends JpaRepository<Person, Long>, PersonRepoCustom {

        Person findTopByUsernameIgnoreCase(String username);

        List<Person> findAllByOrderByUsername();

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

        List<Person> findByUsernameLikeIgnoreCase(String username, Sort sort);
        // TODO: more versatile might be using Example matchers
        // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
        // See: https://vaadin.com/forum/thread/16031323
        // findAll(Example.of(userProbe, userMatcher));

        int countByUsernameLikeIgnoreCase(String username);

}