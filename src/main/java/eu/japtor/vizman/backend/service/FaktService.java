package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Fakt;
import eu.japtor.vizman.backend.entity.Zak;

import java.util.List;

public interface FaktService {

    Fakt saveFakt(Fakt fakt);

    boolean deleteFakt(Fakt fakt);

    Fakt getFakt(Long id);

    List<Fakt> fetchAll();

    long countAll();
}
