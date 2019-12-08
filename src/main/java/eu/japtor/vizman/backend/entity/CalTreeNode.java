package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface CalTreeNode<T extends CalTreeNode> {

    Long getId();
    Integer getYr();
    void setYr(Integer yr);
    YearMonth getYm();
    BigDecimal getYearFondDays();
    BigDecimal getYearFondHours();
    BigDecimal getMonthFondDays();
    BigDecimal getMonthFondHours();

    CalTreeNode getParent();
}
