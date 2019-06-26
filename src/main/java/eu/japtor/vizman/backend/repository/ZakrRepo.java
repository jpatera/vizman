package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.entity.Zakr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ZakrRepo extends JpaRepository<Zakr, Long> {

    List<Zakr> findAllByOrderByRokDescCkontDescCzakDesc();

}
