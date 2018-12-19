package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.util.List;

public interface KontZakTreeAware <T extends KontZakTreeAware> {

    String getCkont();
    Integer getCzak();
    ZakTyp getTyp();
    Boolean getArch();
    String getObjednatel();
    String getText();
    BigDecimal getHonorar();
    Mena getMena();
    List<T> getNodes();
}
