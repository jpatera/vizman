package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.DochMes;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakrListView;

import java.time.YearMonth;
import java.util.List;

public interface DochMesService {

    public List<DochMes> fetchRepDochMesForPersonAndYm(Long personId, YearMonth dochYm);

}
