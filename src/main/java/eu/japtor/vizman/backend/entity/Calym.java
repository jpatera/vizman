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
  public static final String SORT_PROP_CALYM = "calYm";

  @Column(
          name = "CAL_YM",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth calYm;

  private BigDecimal pracFondYm;


  public YearMonth getCalYm() {
    return calYm;
  }

  public void setCalYm(YearMonth calYm) {
    this.calYm = calYm;
  }


  public BigDecimal getPracFondYm() {
    return pracFondYm;
  }

  public void setPracFondYr(BigDecimal pracFondYm) {
    this.pracFondYm = pracFondYm;
  }

}
