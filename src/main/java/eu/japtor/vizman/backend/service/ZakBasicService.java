package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakBasicListView;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.util.List;

public interface ZakBasicService {

    List<ZakBasic> fetchAndCalcByFiltersDescOrder(ZakBasicListView.ZakBasicFilterParams zakBasicFilterParams);

}