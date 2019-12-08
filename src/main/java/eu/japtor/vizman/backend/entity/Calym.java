package eu.japtor.vizman.backend.entity;


import eu.japtor.vizman.backend.utils.VzmFormatUtils;

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

  @Override
  public String getMonthLocal() {
      return VzmFormatUtils.monthLocalizedFormatter.format(ym.atDay(1));
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

  public Calym() {
  }

  public Calym(YearMonth ym, BigDecimal monthFondDays, Caly caly) {
    this.ym = ym;
    this.monthFondDays = monthFondDays;
    this.monthFondHours = monthFondDays.multiply(BigDecimal.valueOf(8));
    this.caly = caly;
  }

//  @Override
//  public Long getNodeId() {
//    return getId();
//  }

  @Override
  public Integer getYr() {
    return null;
//    return 0;
  }

  @Override
  public void setYr(Integer yr) {
//    return null;
//    return 0;
  }

  @Override
  public BigDecimal getYearFondDays() {
    return null;
  }

  @Override
  public BigDecimal getYearFondHours() {
    return null;
  }

  @Override
  public CalTreeNode getParent() {
    return getCaly();
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
//        if (getId() == null) {
//            return super.hashCode();
//        }
//        return 31 + getId().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof AbstractGenIdEntity)) return false;
    return getId() != null && getId().equals(((AbstractGenIdEntity) other).getId());
//		if (id == null) {
//			// New entities are only equal if the instance is the same
//			return super.equals(other);
//		}
  }
}
