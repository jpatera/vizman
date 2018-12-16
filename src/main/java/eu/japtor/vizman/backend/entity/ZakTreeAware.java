package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ZakTreeAware {

    String getCkont();
    Integer getCzak();
    String getFirma();
    String getText();
    BigDecimal getHonorar();
    List<ZakTreeAware> getNodes();
    void setNodes(List<ZakTreeAware> subNodes);

    //    Collection<? extends ZakTreeAware> getNodes();

//        void setNodes(List<? extends ZakTreeAware> nodes);

}
