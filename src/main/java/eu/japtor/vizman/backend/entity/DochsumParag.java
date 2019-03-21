package eu.japtor.vizman.backend.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "DOCHSUM_PARAG")

public class DochsumParag extends AbstractGenIdEntity {

  private Long personId;
  private LocalDate dsDate;

  @Column(
          name = "DS_YM",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth dsYm;

  private Long paragId;
  private String cparag;
  private String username;
  private BigDecimal dspWorkOff;


  public DochsumParag() {
      super();
  }

  public DochsumParag(Long personId, LocalDate dsDate, Long paragId) {
    this.personId = personId;
    this.dsDate = dsDate;
    this.paragId = paragId;
  }


  public Long getPersonId() {
    return personId;
  }
  public void setPersonId(Long personId) {
    this.personId = personId;
  }

  public LocalDate getDsDate() {
    return dsDate;
  }
  public void setPersonId(LocalDate dsDate) {
    this.dsDate = dsDate;
  }

  public YearMonth getDsYm() {
    return dsYm;
  }
  public void setDsYm(YearMonth dsYm) {
    this.dsYm = dsYm;
  }

  public Long getParagId() {
    return paragId;
  }
  public void setParagId(long zakId) {
    this.paragId = zakId;
  }

  public String getCparag() {
    return cparag;
  }
  public void setCparag(String cparag) {
    this.cparag = cparag;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  public BigDecimal getDspWorkOff() {
    return dspWorkOff;
  }
  public void setDspWorkOff(BigDecimal dspWork) {
    this.dspWorkOff = dspWork;
  }
}
