package eu.japtor.vizman.backend.entity;

import java.time.LocalDate;

public interface CalyHolTreeNode<T extends CalyHolTreeNode> {

    Long getId();
    Integer getVersion();
    Integer getYr();
    void setYr(Integer yr);
    LocalDate getHolDate();
    void setHolDate(LocalDate holDate);
    String getHolText();
    void setHolText(String holText);
}
