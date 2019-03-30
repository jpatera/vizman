package eu.japtor.vizman.backend.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "PRUH")
public class Pruh extends AbstractGenIdEntity {
  public final static Integer PRUH_STATE_UNDEFINED = 0;
  public final static Integer PRUH_STATE_UNLOCKED = 10;
  public final static Integer PRUH_STATE_LOCKED = 20;

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
  @Column(name = "PERSON_ID")
  private Long personId;

  @Basic
  @Column(name = "STATE")
  private Integer state;

  @Basic
  @Column(name = "PERSON_PARAG")
  private BigDecimal personParag;

  @Basic
  @Column(name = "PERSON_FOND")
  private BigDecimal personFond;


  public YearMonth getYm() {
    return ym;
  }
  public void setYm(YearMonth calYm) {
    this.ym = calYm;
  }

  public Long getPersonId() {
    return personId;
  }
  public void setPersonId(Long personId) {
    this.personId = personId;
  }

  public Integer getState() {
    return state;
  }
  public void setState(Integer state) {
    this.state = state;
  }

  public BigDecimal getPersonParag() {
    return personFond;
  }
  public void setPersonParag(BigDecimal personParag) {
    this.personParag = personParag;
  }

  public BigDecimal getPersonFond() {
    return personFond;
  }
  public void setPersonFond(BigDecimal personFond) {
    this.personFond = personFond;
  }
}
