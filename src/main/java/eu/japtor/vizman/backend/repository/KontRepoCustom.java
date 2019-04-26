package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Kont;

import java.util.List;

public interface KontRepoCustom {

    String[] getAllObjednatelArray();

    void detachKont(Kont kont);

//    List<Kont> findHavingAllZaksArchived();

//    List<Kont> findHavingSomeZaksActive();

}
