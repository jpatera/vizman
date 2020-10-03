package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.KontVw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KontVwRepo extends JpaRepository<KontVw, Long>, KontRepoCustom {

    KontVw findTopById(Long id);

    List<KontVw> findAllByOrderByCkontDescRokDesc();

}
