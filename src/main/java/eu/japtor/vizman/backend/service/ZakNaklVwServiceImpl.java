package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.ZakYmNaklVw;
import eu.japtor.vizman.backend.entity.ZaknNaklVw;
import eu.japtor.vizman.backend.repository.ZakYmNaklVwRepo;
import eu.japtor.vizman.backend.repository.ZakNaklVwRepo;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ZakNaklVwServiceImpl implements ZakNaklVwService, HasLogger {

    private ZakNaklVwRepo zakNaklVwRepo;

    @Autowired
    public ZakNaklVwServiceImpl(ZakNaklVwRepo zakNaklVwRepo) {
        super();
        this.zakNaklVwRepo = zakNaklVwRepo;
    }

    @Autowired
    ZakYmNaklVwRepo zakYmNaklVwRepo;


    @Override
    public List<ZaknNaklVw> fetchByZakId(final Long zakId, ZakrListView.ZakrParams zakrParams) {
        List<ZaknNaklVw> zakns = zakNaklVwRepo.findByZakIdOrderByPersonIdAscDatePruhDesc(zakId);
        zakns.stream()
                .filter(zn -> null != zn.getWorkPruh() && zn.getWorkPruh().compareTo(BigDecimal.ZERO) != 0)
                .forEach(zn -> {
                    zn.setNaklPojist(zn.calcNaklPojist(zakrParams.getKoefPojist()));
                    zn.setNaklRezie(zn.calcNaklPojist(zakrParams.getKoefRezie()));
                })
        ;
        return zakns;
    }

    @Override
    public List<ZakYmNaklVw> fetchByZakIdsSumByYm(final List<Long> zakIds, ZakrListView.ZakrParams zakrParams) {
        List<ZakYmNaklVw> zakns = zakYmNaklVwRepo.findByZakIdsSumByYm(zakIds);
        zakns.stream()
                .filter(zn -> null != zn.getWorkPruh() && zn.getWorkPruh().compareTo(BigDecimal.ZERO) != 0)
                .forEach(zn -> {
                    zn.setNaklPojist(zn.calcNaklPojist(zakrParams.getKoefPojist()));
                    zn.setNaklRezie(zn.calcNaklPojist(zakrParams.getKoefRezie()));
                })
        ;
        return zakns;
    }
}
