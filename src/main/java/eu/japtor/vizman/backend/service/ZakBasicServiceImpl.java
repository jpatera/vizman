package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.ui.views.ZakBasicListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ZakBasicServiceImpl implements ZakBasicService, HasLogger {

    private ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicServiceImpl(ZakBasicRepo zakBasicRepo) {
        super();
        this.zakBasicRepo = zakBasicRepo;
    }

    @Override
    public List<ZakBasic> fetchByFiltersDescOrder(ZakBasicListView.ZakBasicFilter zakBasicFilter) {
        List<ZakBasic> zakBasics = zakBasicRepo.findZakBasicByArchAndDigiAndCkontAndRokAndSkupina(
                zakBasicFilter.getArch()
                , zakBasicFilter.getDigi()
                , zakBasicFilter.getCkz()
                , zakBasicFilter.getRokZak()
                , zakBasicFilter.getSkupina()
                , zakBasicFilter.getTextKont()
                , zakBasicFilter.getTextZak()
                , zakBasicFilter.getObjednatel()
        );
        return zakBasics;
    }

    @Override
    public List<ZakBasic> fetchAllDescOrder() {
        return zakBasicRepo.findAll();
    }

}
