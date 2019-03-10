package eu.japtor.vizman.backend.entity;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CALYM")
public class Calym extends AbstractGenIdEntity {

  private Integer calYm;
  private BigDecimal pracFondYm;


  public Integer getCalYm() {
    return calYm;
  }

  public void setCalYr(Integer calYm) {
    this.calYm = calYm;
  }


  public BigDecimal getPracFondYm() {
    return pracFondYm;
  }

  public void setPracFondYr(BigDecimal pracFondYm) {
    this.pracFondYm = pracFondYm;
  }

}
