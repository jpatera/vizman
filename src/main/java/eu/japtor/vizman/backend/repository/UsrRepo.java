package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Usr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRepo extends JpaRepository<Usr, Long>, UsrRepoCustom {

        Usr findTopByUsername(String name);
}
