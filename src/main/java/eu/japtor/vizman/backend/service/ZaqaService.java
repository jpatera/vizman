package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.PersonWage;
import eu.japtor.vizman.backend.entity.Zakr;
import eu.japtor.vizman.backend.entity.Zaqa;
import eu.japtor.vizman.ui.components.Operation;

import java.util.List;

public interface ZaqaService {

    Zaqa saveItem(Zaqa itemToSave, Operation oper);

    boolean deleteItem(Zaqa itemToDelete);

    List<Zaqa> fetchByParentId(Long parentId);
}
