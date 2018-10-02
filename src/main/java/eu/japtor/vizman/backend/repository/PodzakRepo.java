package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Podzak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PodzakRepo extends JpaRepository<Podzak, Long> {

//    @Modifying
//    @Transactional
//    @Query("delete from User u where u.active = false")
//      someMethod...
}
