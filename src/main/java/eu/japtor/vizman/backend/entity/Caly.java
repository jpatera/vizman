package eu.japtor.vizman.backend.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "CALY")
public class Caly extends AbstractGenIdEntity implements CalTreeNode {
  public static final String SORT_PROP_YR = "yr";

  @Basic
  @Column(name = "YR")
  private Integer yr;

  @Basic
  @Column(name = "YEAR_FOND_HOURS")
  private BigDecimal yearFondHours;

  @Basic
  @Column(name = "YEAR_FOND_DAYS")
  private BigDecimal yearFondDays;


  @Override
  public Integer getYr() {
    return yr;
  }

  public void setYr(Integer yr) {
    this.yr = yr;
  }


  public BigDecimal getYearFondHours() {
    return yearFondHours;
  }
  public void setYearFondHours(BigDecimal yearFondHours) {
    this.yearFondHours = yearFondHours;
  }

  public BigDecimal getYearFondDays() {
    return yearFondDays;
  }
  public void setYearFondDays(BigDecimal yearFondDays) {
    this.yearFondDays = yearFondDays;
  }

  // ---------------------------------------------------

  @Override
  public Long getNodeId() {
    return getId();
  }

  @Override
  public YearMonth getYm() {
    return null;
  }

  @Override
  public BigDecimal getFondDays() {
    return getYearFondDays();
  }

  @Override
  public BigDecimal getFondHours() {
    return getYearFondHours();
  }

  @Override
  public CalTreeNode getParent() {
    return null;
  }
}
