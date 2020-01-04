package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "DOCHSUM_ZAK")
public class DochsumZak extends AbstractGenIdEntity {

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

  @Column(name = "DSZ_KOEF_P8")
  private BigDecimal dszKoefP8;

  @Column(name = "DSZ_WORK_P8")
  private BigDecimal dszWorkP8;

  @Column(name = "DSZ_WORK_NORM")
  private Double dszWorkNorm;

  @Column(name = "DSZ_MZDA")
  private BigDecimal dszMzda;

  @Column(name = "DSZ_POJIST")
  private BigDecimal dszPojist;

  @Column(name = "DSZ_MZDA_P8")
  private BigDecimal dszMzdaP8;

  @Column(name = "DSZ_POJIST_P8")
  private BigDecimal dszPojistP8;

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

  public DochsumZak(
          Long personId
          , LocalDate dsDate
          , YearMonth dsYm
          , Long zakId
          , BigDecimal dszWorkPruh
          , BigDecimal dszKoefP8
          , BigDecimal dszWorkP8
          , BigDecimal sazba)
  {
    this.personId = personId;
    this.dsDate = dsDate;
    this.dsYm = dsYm;
    this.zakId = zakId;
    this.dszWorkPruh = dszWorkPruh;
    this.dszKoefP8 = dszKoefP8;
    this.dszWorkP8 = dszWorkP8;
    this.sazba = sazba;
    this.dszMzda = (null == this.sazba || null == this.dszWorkPruh) ? null : this.sazba.multiply(this.dszWorkPruh);
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

  public BigDecimal getDszKoefP8() {
    return dszKoefP8;
  }

  public void setDszKoefP8(BigDecimal dszKoefP8) {
    this.dszKoefP8 = dszKoefP8;
  }

  public BigDecimal getDszWorkP8() {
    return dszWorkP8;
  }

  public void setDszWorkP8(BigDecimal dsWorkP8) {
    this.dszWorkP8 = dsWorkP8;
  }


  public BigDecimal getDszMzda() {
    return dszMzda;
  }

  public void setDszMzda(BigDecimal dsMzda) {
    this.dszMzda = dsMzda;
  }


  // TODO: remove, from DB too ??
  public BigDecimal getDszPojist() {
    return dszPojist;
  }

  public void setDszPojist(BigDecimal dsPojist) {
    this.dszPojist = dsPojist;
  }


  public BigDecimal getDszMzdaP8() {
    return dszMzdaP8;
  }

  public void setDszMzdaP8(BigDecimal dsMzdaP8) {
    this.dszMzdaP8 = dsMzdaP8;
  }


  // TODO: remove, from DB too ??
  public BigDecimal getDszPojistP8() {
    return dszPojistP8;
  }

  public void setDszPojistP8(BigDecimal dsPojistP8) {
    this.dszPojistP8 = dsPojistP8;
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
