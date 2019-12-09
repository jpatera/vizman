package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public interface CalHolTreeNode<T extends CalHolTreeNode> {

    Long getId();
    Integer getYr();
    void setYr(Integer yr);
    LocalDate getHolDate();
    void setHolDate(LocalDate holDate);
    String getHolText();
    void setHolText(String holText);

//    CalHolTreeNode getParent();
}
