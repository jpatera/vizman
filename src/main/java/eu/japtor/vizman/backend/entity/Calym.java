package eu.japtor.vizman.backend.entity;


import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
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

  private BigDecimal monthFond;


  public YearMonth getYm() {
    return ym;
  }

  public void setYm(YearMonth calYm) {
    this.ym = calYm;
  }


  public BigDecimal getMonthFond() {
    return monthFond;
  }

  public void setPracFondYr(BigDecimal pracFondYm) {
    this.monthFond = pracFondYm;
  }

}
