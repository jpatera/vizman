package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.backend.repository.ZakBasicRepo;
import eu.japtor.vizman.backend.repository.ZakRepo;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.ZakSimpleGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class ZakBasicServiceImpl implements ZakBasicService, HasLogger {

    private ZakBasicRepo zakBasicRepo;

    @Autowired
    public ZakBasicServiceImpl(ZakBasicRepo zakBasicRepo) {
        super();
        this.zakBasicRepo = zakBasicRepo;
    }

    @Autowired
    public ZakRepo zakRepo;


    @Override
    public List<ZakBasic> fetchByFiltersDescOrder(ZakSimpleGrid.ZakBasicFilter zakBasicFilter) {
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
    public ZakBasic fetchByIdLazy(final Long id) {
        ZakBasic zakBasic = zakBasicRepo.findById(id).orElse(null);
        if (null == zakBasic) {
            return null;
        } else {
            return zakBasic;
        }
    }


    @Override
    public List<ZakBasic> fetchAllDescOrder() {
        return zakBasicRepo.findAll();
    }

    @Transactional
    @Override
    public ZakBasic saveZakBasic(ZakBasic zakBasic, Operation operation) {
        Zak zak = zakRepo.findById(zakBasic.getId()).orElse(null);
        if (null == zak) {
            return null;
        } else {
            zak.setArch(zakBasic.getArch());
            zak.setDigi(zakBasic.getDigi());
            zak = zakRepo.saveAndFlush(zak);
            return zakBasicRepo.findById(zak.getId()).orElse(null);
        }
    }
}
