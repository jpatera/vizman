package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.KontVw;
import eu.japtor.vizman.ui.components.Operation;

import java.util.List;

public interface KontService {

    Kont saveKont(Kont kont, Operation oper);

    void deleteKont(Kont kont);

    Kont fetchOne(Long id);

//    Kont fetchByObjednatel(String objednatel);

    Kont fetchByCkont(String ckont);

    Kont fetchByText(String text);

    Kont fetchByFolder(String folder);

    List<Kont> fetchAll();

    List<KontVw> fetchAllFromKontVw();

    List<? extends Kont> fetchForReport();

    List<? super Kont> fetchByCkontFilter(String ckont);

    List<? super Kont> fetchByRokFilter(Integer rok);

    List<? super Kont> fetchHavingSomeZaksActiveFilter();

    List<? super Kont> fetchHavingAllZaksArchivedFilter();

    List<? super Kont> fetchHavingNoZaksFilter();

    List<Integer> fetchKontRoks();

    long countAll();

    long getAssignedByKontsCount(Klient klient);

//    List<Kont> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

//    long countByFilter(String filter);
}
