package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Role;

import java.util.List;

public interface KlientService {

    Klient fetchKlientByName(String name);

    Klient saveKlient(Klient klient);

    void deleteKlient(Klient klient);

    List<Klient> fetchAll();

    long countAll();

    List<Klient> fetchBySearchFilter(String serachString, List<QuerySortOrder> sortOrders);

    long countByFilter(String filter);


}
