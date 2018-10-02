package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Podzak;

import java.util.List;

public interface PodzakService {

    Podzak savePodzak(Podzak podzak);

    Podzak getPodzak(Long id);

    List<Podzak> getAllPodzak();

    long countPodzak();
}
