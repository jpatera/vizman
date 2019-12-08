package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface CalTreeNode<T extends CalTreeNode> {

    Long getId();
    Integer getYr();
    void setYr(Integer yr);
    YearMonth getYm();
    String getMonthLocal();
    BigDecimal getYearFondDays();
    BigDecimal getYearFondHours();
    BigDecimal getMonthFondDays();
    BigDecimal getMonthFondHours();

    CalTreeNode getParent();
}
