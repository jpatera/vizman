package eu.japtor.vizman.backend.repository;

import eu.japtor.vizman.backend.entity.Zak;

public interface ZakRepoCustom {

    Zak[] getNotArchivedArray();

    int getCountByIdKontAndCzak(Long idKont, Integer czak);

}
