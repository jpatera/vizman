package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "PERSON_WAGE")
public class PersonWage extends AbstractGenIdEntity implements HasItemType {

  @Override
  @Transient
  public ItemType getTyp() {
    return ItemType.WAGE;
  }

  @Basic
  @Column(name = "USERNAME")
  private String username;

  @Column(
          name = "YM_FROM",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth ymFrom;

  @Column(
          name = "YM_TO",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth ymTo;


  @Column(name = "WAGE")
  private BigDecimal wage;



//  @ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(name = "PERSON_ID")
//  private Person person;
//
//  public Person getPerson() {
//    return person;
//  }
//
//  public void setPerson(Person person) {
//    this.person = person;
//  }

  @Column(name = "PERSON_ID")
  private Long personId;



  public PersonWage() {
    super();
  }




  public Long getPersonId() {
    return personId;
  }

  public void setPersonId(Long personId) {
    this.personId = personId;
  }


  public YearMonth getYmFrom() {
    return ymFrom;
  }
  public void setYmFrom(YearMonth ymFrom) {
    this.ymFrom = ymFrom;
  }

  @Transient
  public int getYearFrom() {
    return ymFrom.getYear();
  }

  @Transient
  public int getMonthFrom() {
    return ymFrom.getMonth().getValue();
  }

  public YearMonth getYmTo() {
    return ymTo;
  }
  public void setYmTo(YearMonth ymTo) {
    this.ymTo = ymTo;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public BigDecimal getWage() {
    return wage;
  }
  public void setWage(BigDecimal wage) {
    this.wage = wage;
  }

}
