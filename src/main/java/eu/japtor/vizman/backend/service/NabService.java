package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.NabView;
import eu.japtor.vizman.ui.components.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NabService {

    NabView fetchOne(Long id);

    NabView saveNab(NabView nabViewToSave, Operation oper);

    boolean deleteNab(NabView nabView);
}
