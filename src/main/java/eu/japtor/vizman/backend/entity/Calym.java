package eu.japtor.vizman.backend.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "CALYM")
public class Calym extends AbstractGenIdEntity implements CalTreeNode {
  public static final String SORT_PROP_YM = "ym";

  @Column(
          name = "YM",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth ym;

  @Basic
  @Column(name = "MONTH_FOND_HOURS")
  private BigDecimal monthFondHours;

  @Basic
  @Column(name = "MONTH_FOND_DAYS")
  private BigDecimal monthFondDays;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="CALY_ID", unique=true, nullable=false, insertable=false, updatable=false)
//    @JoinColumn(name = "id", nullable = false)
//    @MapsId
//    @OrderBy("cfakt DESC")
  private Caly caly;


  @Override
  public YearMonth getYm() {
    return ym;
  }

  public void setYm(YearMonth calYm) {
    this.ym = calYm;
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


  public Caly getCaly() {
    return caly;
  }
  public void setCaly(Caly caly) {
    this.caly = caly;
  }

// ---------------------------------------

  @Override
  public Long getNodeId() {
    return getId();
  }

  @Override
  public Integer getYr() {
    return null;
  }

  @Override
  public BigDecimal getFondDays() {
    return getMonthFondDays();
  }

  @Override
  public BigDecimal getFondHours() {
    return getMonthFondHours();
  }

  @Override
  public CalTreeNode getParent() {
    return getCaly();
  }
}
