package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaktRepo extends JpaRepository<Fakt, Long>, FaktRepoCustom {

//    List<Fakt> findByZakIdOrderByCkontDesc(Long Cfakt);

}
