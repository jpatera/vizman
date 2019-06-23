package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.ui.components.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface ZakService {

    Zak saveZak(Zak zak, Operation oper);

    void deleteZak(Zak zak);

    Zak fetchOne(Long id);

    List<Zak> fetchAll();

    List<Zak> fetchAllDescOrder();

    List<Zak> fetchByRokDescOrder(Integer rok);

    List<Zak> fetchByIds(List<Long> ids);

    long countAll();

    List<Integer> fetchZakRoks();

    BigDecimal getSumPlneni(Long faktId);

    Integer getNewCfakt(Zak zak);

    Integer getNewCfakt(Long zakId);

    boolean zakIdExistsInKont(Long kontId, Integer czak);

}
