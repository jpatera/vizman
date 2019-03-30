package eu.japtor.vizman.backend.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "CALYM")
public class Calym extends AbstractGenIdEntity {
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


}
