package eu.japtor.vizman.backend.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "DOCHSUM_ZAK")

public class DochsumZak  extends AbstractGenIdEntity {

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

  private Long zakId;
  private String ckontOrig;
  private String username;
  private BigDecimal dsWork;
  private Double dsWorkNorm;
  private BigDecimal dsMzda;
  private BigDecimal dsPojist;
  private BigDecimal dsMzdas;
  private BigDecimal dsPojists;
  private BigDecimal sazba;
  private String tmp;



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


  public Long getZakId() {
    return zakId;
  }

  public void setZakId(long zakId) {
    this.zakId = zakId;
  }


  public String getCkontOrig() {
    return ckontOrig;
  }

  public void setCkontOrig(String ckontOrig) {
    this.ckontOrig = ckontOrig;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public BigDecimal getDsWork() {
    return dsWork;
  }

  public void setDsWork(BigDecimal dsWork) {
    this.dsWork = dsWork;
  }


  public Double getDsWorkNorm() {
    return dsWorkNorm;
  }

  public void setDsWorkNorm(Double dsWorkNorm) {
    this.dsWorkNorm = dsWorkNorm;
  }


  public BigDecimal getDsMzda() {
    return dsMzda;
  }

  public void setDsMzda(BigDecimal dsMzda) {
    this.dsMzda = dsMzda;
  }


  public BigDecimal getDsPojist() {
    return dsPojist;
  }

  public void setDsPojist(BigDecimal dsPojist) {
    this.dsPojist = dsPojist;
  }


  public BigDecimal getDsMzdas() {
    return dsMzdas;
  }

  public void setDsMzdas(BigDecimal dsMzdas) {
    this.dsMzdas = dsMzdas;
  }


  public BigDecimal getDsPojists() {
    return dsPojists;
  }

  public void setDsPojists(BigDecimal dsPojists) {
    this.dsPojists = dsPojists;
  }


  public BigDecimal getSazba() {
    return sazba;
  }

  public void setSazba(BigDecimal sazba) {
    this.sazba = sazba;
  }


  public String getTmp() {
    return tmp;
  }

  public void setTmp(String tmp) {
    this.tmp = tmp;
  }

}
