package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

//@Entity
//@Table(name = "CALY")
public class CalyTest implements CalTreeNode {
  public static final String SORT_PROP_YR = "yr";

  @Basic
  @Column(name = "ID")
  private Long id;

  @Basic
  @Column(name = "YR")
  private Integer yr;

  @Basic
  @Column(name = "YEAR_FOND_HOURS")
  private BigDecimal yearFondHours;

  @Basic
  @Column(name = "YEAR_FOND_DAYS")
  private BigDecimal yearFondDays;

  @Transient
  @Column(name = "MONTH_FOND_HOURS")
  private BigDecimal monthFondHours;

  @Transient
  @Column(name = "MONTH_FOND_DAYS")
  private BigDecimal monthFondDays;


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

  public BigDecimal getMonthFondHours() {
    return monthFondHours;
  }
  public void setMonthFondHours(BigDecimal monthFondHours) {
    this.monthFondHours = monthFondHours;
  }

  public BigDecimal getMonthFondDays() {
    return monthFondDays;
  }
  public void setMonthFondDays(BigDecimal monthFondDays) {
    this.monthFondDays = monthFondDays;
  }

  // ---------------------------------------------------



  public CalyTest() {
  }

  public CalyTest(Integer yr, BigDecimal yearFondHours, BigDecimal yearFondDays) {
    this.yr = yr;
    this.yearFondHours = yearFondHours;
    this.yearFondDays = yearFondDays;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

//  @Override
//  public Long getNodeId() {
//    return getId();
//  }

  @Override
  public YearMonth getYm() {
    return null;
  }

  @Override
  public String getMonthLocal() {
    return null;
  }

  @Override
  public CalTreeNode getParent() {
    return null;
  }
}
