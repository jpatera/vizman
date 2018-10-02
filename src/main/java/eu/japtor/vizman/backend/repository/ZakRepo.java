package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZakRepo extends JpaRepository<Zak, Long> {

    Zak findTopByFirma(String firma);
}
