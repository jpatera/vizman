package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.ui.views.NabListView;

import java.util.List;

public interface NabService {

    List<Nab> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams);

}
