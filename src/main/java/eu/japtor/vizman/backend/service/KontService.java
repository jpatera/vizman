package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Kont;

import java.util.List;

public interface KontService {

    Kont getById(Long id);

    Kont saveZak(Kont kont);

    void deleteZak(Kont kont);

    Kont getByObjednatel(String objednatel);

    List<Kont> fetchAll();

    long countAll();

    List<Kont> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);
}
