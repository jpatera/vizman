package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.ui.components.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface ZakrService {

    Zakr saveZakr(Zakr zakr, Operation oper);

    Zakr fetchOne(Long id);

    List<Zakr> fetchAllDescOrder();

    List<Zakr> fetchByRokDescOrder(Integer rok);

    long countAll();

    List<Integer> fetchZakRoks();
}
