package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.ui.views.ZakBasicListView;

import java.util.List;

public interface ZakBasicService {

    List<ZakBasic> fetchByFiltersDescOrder(ZakBasicListView.ZakBasicFilter zakBasicFilter);

    List<ZakBasic> fetchAllDescOrder();

}
