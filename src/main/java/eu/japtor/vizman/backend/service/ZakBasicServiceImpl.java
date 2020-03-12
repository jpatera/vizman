package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Mena;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.repository.ZakrRepo;
import eu.japtor.vizman.backend.repository.ZaqaRepo;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakBasicListView;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;


@Service
public class ZakBasicServiceImpl implements ZakBasicService, HasLogger {

    private ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicServiceImpl(ZakBasicRepo zakBasicRepo) {
        super();
        this.zakBasicRepo = zakBasicRepo;
    }

    @Override
    public List<ZakBasic> fetchAndCalcByFiltersDescOrder(ZakBasicListView.ZakBasicFilterParams zakBasicFilterParams) {
        List<ZakBasic> zakBasics = zakBasicRepo.findZakBasicByArchAndDigiAndCkontAndRokAndSkupina(
                zakBasicFilterParams.getArch()
                , zakBasicFilterParams.getDigi()
                , zakBasicFilterParams.getCkz()
                , zakBasicFilterParams.getRokZak()
                , zakBasicFilterParams.getSkupina()
        );
        return zakBasics;
    }
}
