package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.NabVw;
import eu.japtor.vizman.backend.repository.NabViewRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NabViewServiceImpl implements NabViewService, HasLogger {

    private NabViewRepo nabViewRepo;

    @Autowired
    public NabViewServiceImpl(NabViewRepo nabViewRepo) {
        super();
        this.nabViewRepo = nabViewRepo;
    }

//    @Override
//    public List<NabVw> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams) {
//        List<NabVw> nabs = nabViewRepo.findNabByRokAndText(
//                nabFilterParams.getRok()
//                , nabFilterParams.getText()
//        );
//        return nabs;
//    }

    @Override
    public Page<NabVw> fetchByNabFilter(NabViewFilter nabViewFilter, List<QuerySortOrder> sortOrders, Pageable pageable) {
        if (nabViewFilter == null) {
            return nabViewRepo.findAll(pageable);
        } else {
            Page<NabVw> pg = nabViewRepo.findAll(Example.of(NabVw.getInstanceFromFilter(nabViewFilter), getNabMatcher()), pageable);
            return pg;
//            return nabViewRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public List<NabVw> fetchFilteredList(NabViewFilter nabViewFilter) {
        if (nabViewFilter == null) {
            return nabViewRepo.findAll();
        } else {
            return nabViewRepo.findAll(Example.of(NabVw.getInstanceFromFilter(nabViewFilter)));
        }
    }

    @Override
    public long countByNabFilter(NabViewFilter nabViewFilter) {
        if (nabViewFilter == null) {
            return nabViewRepo.count();
        } else {
            return nabViewRepo.count(Example.of(NabVw.getInstanceFromFilter(nabViewFilter), getNabMatcher()));
        }
    }


    private ExampleMatcher getNabMatcher()  {
        return ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("rok", new ExampleMatcher.GenericPropertyMatcher().exact())
                .withMatcher("cnab", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().startsWith())
                .withMatcher("ckont", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                .withMatcher("vz", new ExampleMatcher.GenericPropertyMatcher().exact())
                .withMatcher("text", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                .withMatcher("objednatelName", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                .withMatcher("poznamka", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                ;
    }


    @Override
    public NabVw fetchNabByCnab(String cnab) {
        return nabViewRepo.findTopByCnab(cnab);
    }

    @Override
    public List<Integer> fetchRokList() {
        return nabViewRepo.findNabRokAll();
    };

    @Override
    public NabVw fetchOne(Long id) {
        return nabViewRepo.findTopById(id);
    }

    @Override
    public NabVw saveNab(NabVw itemToSave, Operation oper) {
        try {
            NabVw nabVwSaved = nabViewRepo.save(itemToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , nabVwSaved.getTyp().name(), oper.name());

            return nabVwSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteNab(NabVw nabVwToDelete) {
        try {
            nabViewRepo.delete(nabVwToDelete);
            getLogger().info("{} deleted: {}, {}", nabVwToDelete.getTyp().name(), nabVwToDelete.getCnab(), nabVwToDelete.getText());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}, {}";
            getLogger().error(errMsg, nabVwToDelete.getTyp().name(), nabVwToDelete.getCnab(), nabVwToDelete.getText(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<NabVw> fetchAll() {
        return nabViewRepo.findAllByOrderByCnabDescTextAsc();
    }

}
