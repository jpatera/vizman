package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;

import java.util.List;

public interface ZakService {

    Zak saveZak(Zak zak);

    Zak getZak(Long id);

    List<Zak> getAllZak();

    long countZak();
}
