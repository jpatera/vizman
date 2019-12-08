package eu.japtor.vizman.backend.entity;

import org.springframework.data.annotation.Transient;

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



  public Caly() {
  }

  public Caly(Integer yr, BigDecimal yearFondDays) {
    this.yr = yr;
    this.yearFondDays = yearFondDays;
    this.yearFondHours = yearFondDays.multiply(BigDecimal.valueOf(8));
  }

  public Caly(Integer yr, BigDecimal yearFondHours, BigDecimal yearFondDays) {
    this.yr = yr;
    this.yearFondHours = yearFondHours;
    this.yearFondDays = yearFondDays;
  }

//  @Override
//  public Long getNodeId() {
//    return getId();
//  }

  @Transient
  public YearMonth getYm() {
    return null;
  }

  @Transient
  public String getMonthLocal() {
    return null;
  }

  @Override
  public BigDecimal getMonthFondDays() {
    return null;
  }

  @Override
  public BigDecimal getMonthFondHours() {
    return null;
  }

  @Override
  public CalTreeNode getParent() {
    return null;
  }


// ========================================

  public static Caly getEmptyInstance() {
    Caly c = new Caly();
//    c.set...(null);
    return c;
  }

// ========================================

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
