package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Kont;
import eu.japtor.vizman.backend.entity.Zak;
import eu.japtor.vizman.ui.components.Operation;

import java.math.BigDecimal;
import java.util.List;

public interface ZakService {

    Integer getNewCfakt(Zak zak);

    Integer getNewCfakt(Long zakId);

    BigDecimal getSumPlneni(Long faktId);

    Zak saveZak(Zak zak, Operation oper);

    boolean deleteZak(Zak zak);

    boolean zakIdExistsInKont(Long kontId, Integer czak);

    Zak getById(Long id);

    List<Zak> fetchAll();

    List<Zak> fetchByIds(List<Long> ids);

    long countAll();
}
