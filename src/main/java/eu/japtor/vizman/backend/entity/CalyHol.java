package eu.japtor.vizman.backend.entity;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CALY_HOL")
public class CalyHol extends AbstractGenIdEntity implements CalyHolTreeNode, HasItemType {

  @Transient
  private ItemType typ;
  private Integer yr;
  private LocalDate holDate;
  private String holText;

  @Override
  public ItemType getTyp() {
    return ItemType.SVAT;
  }

  public Integer getYr() {
    return yr;
  }

  public void setYr(Integer yr) {
    this.yr = yr;
  }

  @Override
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

  public CalyHol() {
    super();
    this.typ = ItemType.SVAT;
  }

  public static CalyHol getEmptyInstance() {
    CalyHol ch = new CalyHol();
//    ch.set...(null);
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
