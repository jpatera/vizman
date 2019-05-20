package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.ui.components.Operation;

import java.util.List;

public interface KontService {

    Kont saveKont(Kont kont, Operation oper);

    void deleteKont(Kont kont);

    Kont fetchOne(Long id);

    Kont fetchByObjednatel(String objednatel);

    Kont fetchByCkont(String ckont);

    Kont fetchByText(String text);

    Kont fetchByFolder(String folder);

    List<? super Kont> fetchAll();

    List<? super Kont> fetchHavingSomeZaksActive();

    List<? super Kont> fetchHavingAllZaksArchived();

    List<? super Kont> fetchHavingNoZaks();

    long countAll();

    List<Kont> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);
}
