package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.KontView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KontViewRepo extends JpaRepository<KontView, Long>, KontRepoCustom {

    KontView findTopById(Long id);

    List<KontView> findAllByOrderByCkontDescRokDesc();

}
