package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.app.HasLogger;
import eu.japtor.vizman.backend.entity.Mena;
import eu.japtor.vizman.backend.entity.Zakn;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.backend.repository.ZaknRepo;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.views.ZakrListView;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@Service
public class ZaknServiceImpl implements ZaknService, HasLogger {

    private ZaknRepo zaknRepo;

    @Autowired
    public ZaknServiceImpl(ZaknRepo zaknRepo) {
        super();
        this.zaknRepo = zaknRepo;
    }

    @Override
    public List<Zakn> fetchByZakId(final Long zakId, ZakrListView.ZakrParams zakrParams) {
        List<Zakn> zakns = zaknRepo.findByZakIdOrderByPersonIdAscDatePruhDesc(zakId);
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
    public List<Zakn> fetchByZakIdSumByYm(final Long zakId, ZakrListView.ZakrParams zakrParams) {
        List<Zakn> zakns = zaknRepo.findByZakIdSumByYm(zakId);
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
