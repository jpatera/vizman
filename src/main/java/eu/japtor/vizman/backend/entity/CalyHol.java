package eu.japtor.vizman.backend.entity;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "CALY_HOL")
public class CalyHol extends AbstractGenIdEntity {

  private Integer yr;
  private LocalDate holDate;
  private String holText;


  public Integer getCalYr() {
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
}
