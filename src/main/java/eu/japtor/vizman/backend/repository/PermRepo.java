package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Perm;


public interface PermRepo {

    Perm findTopByName(String name);
}