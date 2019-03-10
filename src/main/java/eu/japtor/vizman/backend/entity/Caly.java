package eu.japtor.vizman.backend.entity;

import eu.japtor.vizman.backend.entity.AbstractGenIdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "CALY")
public class Caly extends AbstractGenIdEntity {

  private Integer calYr;
  private BigDecimal pracFondYr;


  public Integer getCalYr() {
    return calYr;
  }

  public void setCalYr(Integer calYr) {
    this.calYr = calYr;
  }


  public BigDecimal getPracFondYr() {
    return pracFondYr;
  }

  public void setPracFondYr(BigDecimal pracFondYr) {
    this.pracFondYr = pracFondYr;
  }

}
