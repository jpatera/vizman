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

  @Column(name = "DSZ_WORK_PRUH")
  private BigDecimal dszWorkPruh;

  @Column(name = "DSZ_WORK_NORM")
  private Double dszWorkNorm;

  @Column(name = "DSZ_MZDA")
  private BigDecimal dszMzda;

  @Column(name = "DSZ_POJIST")
  private BigDecimal dszPojist;

  @Column(name = "DSZ_MZDAS")
  private BigDecimal dszMzdas;

  @Column(name = "DSZ_POJISTS")
  private BigDecimal dszPojists;

  private BigDecimal sazba;
  private String tmp;


  public DochsumZak() {
      super();
  }

  public DochsumZak(Long personId, LocalDate dsDate, Long zakId) {
    this.personId = personId;
    this.dsDate = dsDate;
    this.zakId = zakId;
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


  public BigDecimal getDszWorkPruh() {
    return dszWorkPruh;
  }

  public void setDszWorkPruh(BigDecimal dszWorkPruh) {
    this.dszWorkPruh = dszWorkPruh;
  }


  public Double getDszWorkNorm() {
    return dszWorkNorm;
  }

  public void setDszWorkNorm(Double dsWorkNorm) {
    this.dszWorkNorm = dsWorkNorm;
  }


  public BigDecimal getDszMzda() {
    return dszMzda;
  }

  public void setDszMzda(BigDecimal dsMzda) {
    this.dszMzda = dsMzda;
  }


  public BigDecimal getDszPojist() {
    return dszPojist;
  }

  public void setDszPojist(BigDecimal dsPojist) {
    this.dszPojist = dsPojist;
  }


  public BigDecimal getDszMzdas() {
    return dszMzdas;
  }

  public void setDszMzdas(BigDecimal dsMzdas) {
    this.dszMzdas = dsMzdas;
  }


  public BigDecimal getDszPojists() {
    return dszPojists;
  }

  public void setDszPojists(BigDecimal dsPojists) {
    this.dszPojists = dsPojists;
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
