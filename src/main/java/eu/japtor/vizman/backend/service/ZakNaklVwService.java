package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.ZakYmNaklVw;
import eu.japtor.vizman.backend.entity.ZaknNaklVw;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.util.List;

public interface ZakNaklVwService {

    List<ZaknNaklVw> fetchByZakId(Long zakId, ZakrListView.ZakrParams zakrParams);

//    List<ZaknNaklVw> fetchByPersonId(final Long personId, ZakrListView.ZakrParams zakrParams);
    List<ZaknNaklVw> fetchByPersonId(final Long personId);

    List<ZakYmNaklVw> fetchByZakIdsSumByYm(List<Long> zakIds, ZakrListView.ZakrParams zakrParams);
}
