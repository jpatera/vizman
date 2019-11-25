package eu.japtor.vizman.backend.entity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface CalTreeNode<T extends CalTreeNode> {

    Long getNodeId();
    Integer getYr();
    YearMonth getYm();
    BigDecimal getFondDays();
    BigDecimal getFondHours();

    CalTreeNode getParent();
}
