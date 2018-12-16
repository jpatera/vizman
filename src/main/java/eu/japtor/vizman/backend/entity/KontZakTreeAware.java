package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface KontZakTreeAware <T extends KontZakTreeAware> {

    String getCkont();
    Integer getCzak();
    String getFirma();
    String getText();
    BigDecimal getHonorar();
    Currency getCurrency();
    List<T> getNodes();
}
