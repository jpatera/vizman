package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.ui.components.ArchIconBox;
import eu.japtor.vizman.ui.components.DigiIconBox;

import java.math.BigDecimal;
import java.util.List;

public interface KzTreeAware<T extends KzTreeAware> {

    String getCkont();
    Integer getCzak();
    ItemType getTyp();
    ArchIconBox.ArchState getArchState();
    DigiIconBox.DigiState getDigiState();
    Klient getKlient();
    String getKlientName();
    String getText();
    BigDecimal getHonorarCisty();
    Mena getMena();
    Integer getRok();
    String getSkupina();
    List<T> getNodes();
    long getBeforeTerms();
    long getAfterTerms();
    boolean isChecked();
    void setChecked(boolean checked);
}
