package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Transactional(readOnly = true)
public interface CinRepo extends JpaRepository<Cin, Long> {

    Cin findByCinKod(String kod);
}
