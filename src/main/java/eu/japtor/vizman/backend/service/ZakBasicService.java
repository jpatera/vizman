package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.ZakBasic;
import eu.japtor.vizman.ui.components.Operation;
import eu.japtor.vizman.ui.components.ZakSimpleGrid;

import java.util.List;

public interface ZakBasicService {

    List<ZakBasic> fetchByFiltersDescOrder(ZakSimpleGrid.ZakBasicFilter zakBasicFilter);

    List<ZakBasic> fetchAllDescOrder();

    ZakBasic saveZakBasic(ZakBasic zakBasic, Operation operation);

    ZakBasic fetchByIdLazy(Long id);

}
