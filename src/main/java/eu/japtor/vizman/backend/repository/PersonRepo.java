package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Person;
import eu.japtor.vizman.backend.entity.PersonState;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PersonRepo extends JpaRepository<Person, Long>, PersonRepoCustom {

        Person findTopByUsernameIgnoreCase(String username);

        // TODO: An alternative is to use getOne(Long id) which does not need to be explicitely specified in  repo.
        //       Don't know which one is better. May be findTopById loads eperson wages automatically?
        Person findTopById(Long id);

        List<Person> findAllByOrderByUsername();

        List<Person> findByHiddenOrderByUsername(boolean hidden);

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

        List<Person> findByUsernameLikeIgnoreCase(String username, Sort sort);
        // TODO: more versatile might be using Example matchers
        // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
        // See: https://vaadin.com/forum/thread/16031323
        // findAll(Example.of(userProbe, userMatcher));

        int countByUsernameLikeIgnoreCase(String username);

}
