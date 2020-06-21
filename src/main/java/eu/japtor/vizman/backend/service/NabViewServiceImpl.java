package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.NabView;
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
//    public List<NabView> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams) {
//        List<NabView> nabs = nabViewRepo.findNabByRokAndText(
//                nabFilterParams.getRok()
//                , nabFilterParams.getText()
//        );
//        return nabs;
//    }

    @Override
    public Page<NabView> fetchByNabFilter(NabViewFilter nabViewFilter, List<QuerySortOrder> sortOrders, Pageable pageable) {
        if (nabViewFilter == null) {
            return nabViewRepo.findAll(pageable);
        } else {
            Page<NabView> pg = nabViewRepo.findAll(Example.of(NabView.getInstanceFromFilter(nabViewFilter), getNabMatcher()), pageable);
            return pg;
//            return nabViewRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public List<NabView> fetchFilteredList(NabViewFilter nabViewFilter) {
        if (nabViewFilter == null) {
            return nabViewRepo.findAll();
        } else {
            return nabViewRepo.findAll(Example.of(NabView.getInstanceFromFilter(nabViewFilter)));
        }
    }

    @Override
    public long countByNabFilter(NabViewFilter nabViewFilter) {
        if (nabViewFilter == null) {
            return nabViewRepo.count();
        } else {
            return nabViewRepo.count(Example.of(NabView.getInstanceFromFilter(nabViewFilter), getNabMatcher()));
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
                .withMatcher("objednatel", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                .withMatcher("poznamka", new ExampleMatcher.GenericPropertyMatcher().ignoreCase().contains())
                ;
    }


    @Override
    public NabView fetchNabByCnab(String cnab) {
        return nabViewRepo.findTopByCnab(cnab);
    }

    @Override
    public List<Integer> fetchRokList() {
        return nabViewRepo.findNabRokAll();
    };

    @Override
    public NabView fetchOne(Long id) {
        return nabViewRepo.findTopById(id);
    }

    @Override
    public NabView saveNab(NabView itemToSave, Operation oper) {
        try {
            NabView nabViewSaved = nabViewRepo.save(itemToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , nabViewSaved.getTyp().name(), oper.name());

            return nabViewSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteNab(NabView nabViewToDelete) {
        try {
            nabViewRepo.delete(nabViewToDelete);
            getLogger().info("{} deleted: {}, {}", nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}, {}";
            getLogger().error(errMsg, nabViewToDelete.getTyp().name(), nabViewToDelete.getCnab(), nabViewToDelete.getText(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<NabView> fetchAll() {
        return nabViewRepo.findAllByOrderByCnabDescTextAsc();
    }

}
