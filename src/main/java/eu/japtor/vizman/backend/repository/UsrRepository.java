package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Usr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsrRepository extends JpaRepository<Usr, Long> {
}
