package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.NabView;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

//    @Override
//    public List<NabView> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams) {
//        List<NabView> nabs = nabRepo.findNabByRokAndText(
//                nabFilterParams.getRok()
//                , nabFilterParams.getText()
//        );
//        return nabs;
//    }

    @Override
    public Page<NabView> fetchByNabFilter(NabFilter nabFilter, List<QuerySortOrder> sortOrders, Pageable pageable) {
        if (nabFilter == null) {
            return nabRepo.findAll(pageable);
        } else {
            Page<NabView> pg = nabRepo.findAll(Example.of(NabView.getInstanceFromFilter(nabFilter), getNabMatcher()), pageable);
            return pg;
//            return nabRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public long countByNabFilter(NabFilter nabFilter) {
        if (nabFilter == null) {
            return nabRepo.count();
        } else {
            return nabRepo.count(Example.of(NabView.getInstanceFromFilter(nabFilter), getNabMatcher()));
        }
    }


    private ExampleMatcher getNabMatcher()  {
        return ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("rok", new ExampleMatcher.GenericPropertyMatcher().exact())
                .withMatcher("cnab", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                .withMatcher("ckont", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                .withMatcher("vz", new ExampleMatcher.GenericPropertyMatcher().exact())
                .withMatcher("text", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                .withMatcher("objednatel", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                .withMatcher("poznamka", new ExampleMatcher.GenericPropertyMatcher().startsWith())
                ;
    }


    @Override
    public NabView fetchNabByCnab(String cnab) {
        return nabRepo.findTopByCnab(cnab);
    }

    @Override
    public NabView fetchOne(Long id) {
        return nabRepo.findTopById(id);
    }

    @Override
    public NabView saveNab(NabView itemToSave, Operation oper) {
        try {
            NabView nabViewSaved = nabRepo.save(itemToSave);
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
            nabRepo.delete(nabViewToDelete);
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
        return nabRepo.findAllByOrderByCnabDescTextAsc();
    }

}
