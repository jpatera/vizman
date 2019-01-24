package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public interface KzTreeAware<T extends KzTreeAware> {

    String getCkont();
    Integer getCzak();
    ItemType getTyp();
    Boolean getArch();
//    String getObjednatel();
    Klient getKlient();
    String getKlientName();
    String getText();
    BigDecimal getHonorar();
    Mena getMena();
    Integer getRok();
    String getSkupina();
    List<T> getNodes();
    long getBeforeTerms();
    long getAfterTerms();
}
