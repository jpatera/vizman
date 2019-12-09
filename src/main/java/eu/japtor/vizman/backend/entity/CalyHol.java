package eu.japtor.vizman.backend.entity;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "CALY_HOL")
public class CalyHol extends AbstractGenIdEntity implements CalHolTreeNode {

  private Integer yr;
  private LocalDate holDate;
  private String holText;


  public Integer getYr() {
    return yr;
  }

  public void setYr(Integer yr) {
    this.yr = yr;
  }

  public LocalDate getHolDate() {
    return holDate;
  }

  public void setHolDate(LocalDate holDate) {
    this.holDate = holDate;
  }

  public String getHolText() {
    return holText;
  }

  public void setHolText(String holText) {
    this.holText = holText;
  }

// ========================================

  public static CalyHol getEmptyInstance() {
    CalyHol ch = new CalyHol();
//    c.set...(null);
    return ch;
  }


// ========================================

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof AbstractGenIdEntity)) return false;
    return getId() != null && getId().equals(((AbstractGenIdEntity) other).getId());
  }


}
