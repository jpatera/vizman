package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.ui.views.NabListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NabServiceImpl implements NabService, HasLogger {

    private NabRepo nabRepo;

    @Autowired
    public NabServiceImpl(NabRepo nabRepo) {
        super();
        this.nabRepo = nabRepo;
    }

    @Override
    public List<Nab> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams) {
        List<Nab> nabs = nabRepo.findNabByRokAndText(
                nabFilterParams.getRok()
                , nabFilterParams.getText()
        );
        return nabs;
    }
}
