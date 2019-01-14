package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaktRepo extends JpaRepository<Fakt, Long>, FaktRepoCustom {

//    List<Fakt> findByZakIdOrderByCkontDesc(Long Cfakt);

}
