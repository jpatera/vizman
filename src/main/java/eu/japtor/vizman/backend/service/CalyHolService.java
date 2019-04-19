package eu.japtor.vizman.backend.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import eu.japtor.vizman.backend.entity.CalyHol;
import eu.japtor.vizman.backend.entity.Klient;

import java.time.LocalDate;
import java.util.List;

public interface CalyHolService {

    CalyHol fetchCalyHol(LocalDate holDate);

    boolean calyHolÈxist(LocalDate holDate);

}
