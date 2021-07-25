package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Person;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Transactional(readOnly = true)
public interface PersonRepo extends JpaRepository<Person, Long>, QueryByExampleExecutor<Person>, PersonRepoCustom {

        Person findTopByUsernameIgnoreCase(String username);

        // TODO: An alternative is to use getOne(Long id) which does not need to be explicitly specified in  repo.
        //       Don't know which one is better. May be findTopById loads person wages automatically?
        Person findTopById(Long id);

        List<Person> findAllByOrderByUsername();

        List<Person> findByHiddenOrderByUsername(boolean hidden);

        @Query("SELECT p.id as id FROM Person p WHERE "
                + " p.hidden = :hidden"
                + " ORDER BY p.prijmeni"
        )
        LinkedList<Long> findIdsByHidden(
                @Param("hidden") Boolean hidden
        );

//        Page<Product> findByNameLikeIgnoreCase(String name, Pageable page);

        List<Person> findByUsernameLikeIgnoreCase(String username, Sort sort);
        // TODO: more versatile might be using Example matchers
        // See: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example
        // See: https://vaadin.com/forum/thread/16031323
        // findAll(Example.of(userProbe, userMatcher));

        int countByUsernameLikeIgnoreCase(String username);

}
