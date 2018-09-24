package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Usr;

import java.util.List;

public interface UsrService {

    Usr saveUsr(Usr usr);

    Usr getUsr(Long id);

    Usr getUsrByUsername(String username);

    List<Usr> getAllUsr();

    long countUsr();
}
