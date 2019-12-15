package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Caly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

//@Transactional(readOnly = true)
public interface CalyRepo extends JpaRepository<Caly, Long>, QueryByExampleExecutor<Caly> {

    @Query(value = "SELECT DISTINCT yr FROM vizman.caly ORDER BY yr DESC "
            , nativeQuery = true)
    List<Integer> findAllYrList();

    Caly findByYr(Integer yr);

    void deleteByYr(Integer yr);

}
