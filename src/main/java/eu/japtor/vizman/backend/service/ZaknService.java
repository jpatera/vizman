package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.util.List;

public interface ZaknService {

    List<Zakn> fetchByZakId(Long zakId, ZakrListView.ZakrParams zakrParams);
}
