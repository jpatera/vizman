package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.backend.repository.NabRepo;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.NabListView;
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
//    public List<Nab> fetchByFiltersDescOrder(NabListView.NabFilterParams nabFilterParams) {
//        List<Nab> nabs = nabRepo.findNabByRokAndText(
//                nabFilterParams.getRok()
//                , nabFilterParams.getText()
//        );
//        return nabs;
//    }

    @Override
    public Page<Nab> fetchByNabFilter(NabFilter nabFilter, List<QuerySortOrder> sortOrders, Pageable pageable) {
        if (nabFilter == null) {
            return nabRepo.findAll(pageable);
        } else {
            Page<Nab> pg = nabRepo.findAll(Example.of(Nab.getInstanceFromFilter(nabFilter), getNabMatcher()), pageable);
            return pg;
//            return nabRepo.findAll(Example.of(probe, matcher));
        }
    }

    @Override
    public long countByNabFilter(NabFilter nabFilter) {
        if (nabFilter == null) {
            return nabRepo.count();
        } else {
            return nabRepo.count(Example.of(Nab.getInstanceFromFilter(nabFilter), getNabMatcher()));
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
    public Nab fetchNabByCnab(String cnab) {
        return nabRepo.findTopByCnab(cnab);
    }

    @Override
    public Nab fetchOne(Long id) {
        return nabRepo.findTopById(id);
    }

    @Override
    public Nab saveNab(Nab itemToSave, Operation oper) {
        try {
            Nab nabSaved = nabRepo.save(itemToSave);
            getLogger().info("{} saved: [operation: {}]"
                    , nabSaved.getTyp().name(), oper.name());

            return nabSaved;
        } catch (Exception e) {
            String errMsg = "Error while saving {} : [operation: {}]";
            getLogger().error(errMsg, itemToSave.getTyp().name(), oper.name(), e);
            throw new VzmServiceException(errMsg);
        }
    }

    @Override
    public boolean deleteNab(Nab nabToDelete) {
        try {
            nabRepo.delete(nabToDelete);
            getLogger().info("{} deleted: {}, {}", nabToDelete.getTyp().name(), nabToDelete.getCnab(), nabToDelete.getText());
        } catch (Exception e) {
            String errMsg = "Error while deleting {} : {}, {}";
            getLogger().error(errMsg, nabToDelete.getTyp().name(), nabToDelete.getCnab(), nabToDelete.getText(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<Nab> fetchAll() {
        return nabRepo.findAllByOrderByCnabDescTextAsc();
    }

}
