package eu.japtor.vizman.backend.service;

import eu.japtor.vizman.backend.entity.Klient;
import eu.japtor.vizman.backend.entity.Nab;
import eu.japtor.vizman.ui.components.Operation;


public interface NabService {

    Nab fetchOne(Long id);

    Nab saveNab(Nab nabToSave, Operation oper);

    boolean deleteNab(Nab nabToDelete);

    long getAssignedByNabsCount(Klient klient);
}
