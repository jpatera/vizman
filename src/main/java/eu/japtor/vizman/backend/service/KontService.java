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
    List<? extends Kont> fetchTop();

    List<KontVw> fetchAllFromKontVw();

    List<? super Kont> fetchByCkontFilter(String ckont);
    List<? extends Kont> fetchTopByCkontFilter(String ckont);
    List<? extends Kont> fetchTopByCkont(String ckont);

    List<? super Kont> fetchByRokFilter(Integer rok);
    List<? extends Kont> fetchTopByRokFilter(Integer rok);

    List<? super Kont> fetchByObjednatelFilter(String objednatel);
    List<? extends Kont> fetchTopByObjednatelFilter(String objednatel);

    List<? super Kont> fetchHavingSomeZaksActiveFilter();
    List<? extends Kont> fetchTopHavingSomeZaksActiveFilter();

    List<? super Kont> fetchHavingSomeZakAvizosGreen();
    List<? extends Kont> fetchTopHavingSomeZakAvizosGreen();

    List<? super Kont> fetchHavingSomeZakAvizosRed();
    List<? extends Kont> fetchTopHavingSomeZakAvizosRed();

    List<? super Kont> fetchHavingAllZaksArchivedFilter();
    List<? extends Kont> fetchTopHavingAllZaksArchivedFilter();

    List<? super Kont> fetchHavingNoZaksFilter();
    List<? extends Kont> fetchTopHavingNoZaksFilter();

    List<Integer> fetchKontRoks();

    long countAll();

    long getAssignedByKontsCount(Klient klient);

//    List<Kont> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

//    long countByFilter(String filter);
}
