package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Kont;

import java.util.List;

public interface KontService {

    Kont getById(Long id);

    Kont saveKont(Kont kont);

    boolean deleteKont(Kont kont);

    Kont getByObjednatel(String objednatel);

    Kont getByCkont(String ckont);

    Kont getByText(String text);

    Kont getByDocdir(String docdir);

    List<? super Kont> fetchAll();

    long countAll();

    List<Kont> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);
}
