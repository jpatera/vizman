package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Nab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = false)
public interface NabRepo extends JpaRepository<Nab, Long> {

    // TODO: An alternative is to use getOne(Long id) which does not need to be explicitely specified in  repo.
    //       Don't know which one is better. May be findTopById loads sub-items automatically?
    Nab findTopById(Long id);

    int countAllByObjednatel(Klient objednatel);

}
