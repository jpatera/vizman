package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.util.List;

public interface ZakrService {

    Zakr saveZakr(Zakr zakr, Operation oper);

    Zakr fetchOne(Long id);

    Zakr fetchAndCalcOne(Long id, ZakrListView.ZakrParams zakrParams);

    List<Zakr> fetchAndCalcByFiltersDescOrder(ZakrListView.ZakrFilter zakrFilter, ZakrListView.ZakrParams zakrParams);

    List<Zakr> fetchAndCalcAllDescOrder(ZakrListView.ZakrParams zakrParams);

    List<Long> fetchIdsByFiltersDescOrderWithLimit(ZakrListView.ZakrFilter zakrFilter, ZakrListView.ZakrParams zakrParams);

    List<Zakr> fetchByRokDescOrder(Integer rok);

    long countAll();

    List<Integer> fetchZakrRoks();

    void saveZakr(Zakr itemToSave);
}
