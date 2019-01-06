package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.util.List;

public interface KzTreeAware<T extends KzTreeAware> {

    String getCkont();
    Integer getCzak();
    ItemType getTyp();
    Boolean getArch();
    String getObjednatel();
    String getText();
    BigDecimal getHonorar();
    Mena getMena();
    Short getRokzak();
    List<T> getNodes();
    Integer getOverTerms();
}
