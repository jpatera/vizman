package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Cin;
import eu.japtor.vizman.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Transactional(readOnly = true)
public interface CinRepo extends JpaRepository<Cin, Long> {

    Cin findByCinKod(Cin.CinKod cinKod);

//    List<Cin> findByAkceTypOrderByPoradi(String akceTyp);

    @Query(value = "SELECT * FROM VIZMAN.CIN WHERE (AKCE_TYP = 'ZK' OR AKCE_TYP = 'K') AND CIN_KOD <> 'P'  " +
            " ORDER BY PORADI ASC ",  nativeQuery = true)
    List<Cin> getCinsForDochOdchodRadio();
}
