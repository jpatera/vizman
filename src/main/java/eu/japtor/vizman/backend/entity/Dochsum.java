package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "DOCHSUM")
public class Dochsum extends AbstractGenIdEntity {

  @Basic
  @Column(name = "PERSON_ID")
  private Long personId;

  @Basic
  @Column(name = "DS_DATE")
  private LocalDate dsDate;

  @Column(
          name = "DS_YM",
          columnDefinition = "INTEGER"
  )
  @Convert(
          converter = YearMonthIntegerAttributeConverter.class
  )
  private YearMonth dsYm;

  @Column(name = "USERNAME")
  private String username;

  @Column(name = "DS_FROM_FIRST")
  private LocalDateTime dsFromFirst;

  @Column(name = "DS_TO_LAST")
  private LocalDateTime dsToLast;

  @Column(name = "DS_WORK")
  private BigDecimal dsWork;

  @Column(name = "DS_WORK_RED")
  private BigDecimal dsWorkRed;

  @Column(name = "DS_WORK_PRUH")
  private BigDecimal dsWorkPruh;

  @Basic
  @Column(name = "DS_OBED_AUT")
  private BigDecimal dsObedAut;

  @Basic
  @Column(name = "OBED_KRATKY")
  private Boolean obedKratky;

  @Basic
  @Column(name = "DS_OBED_MAN")
  private BigDecimal dsObedMan;

  @Basic
  @Column(name = "DS_OBED")
  private BigDecimal dsObed;

  @Basic
  @Column(name = "DS_NEM")
  private BigDecimal dsNem;

  @Basic
  @Column(name = "DS_DOV")
  private BigDecimal dsDov;

  @Basic
  @Column(name = "DS_LEK")
  private BigDecimal dsLek;

  @Basic
  @Column(name = "DS_VOL")
  private BigDecimal dsVol;

  @Basic
  @Column(name = "DS_NA")
  private BigDecimal dsNa;

  @Basic
  @Column(name = "DS_NA_DATUM")
  private LocalDate dsNaDatum;

  @Basic
  @Column(name = "DS_VIK")
  private BigDecimal dsVik;

  @Basic
  @Column(name = "RS")
  private Boolean rs;

  @Basic
  @Column(name = "TMP")
  private String tmp;


  public Dochsum() {}

  public Dochsum(Person dochPerson, LocalDate dochDate) {
    this.personId = dochPerson.getId();
    this.username = dochPerson.getUsername();
    this.dsDate = dochDate;
    this.dsYm = YearMonth.of(dochDate.getYear(), dochDate.getMonth());
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
  public void setDsDate(LocalDate dochDate) {
    this.dsDate = dochDate;
  }

  public YearMonth getDsYm() {
    return dsYm;
  }
  public void setDsYm(YearMonth dsYm) {
    this.dsYm = dsYm;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDateTime getDsFromFirst() {
    return dsFromFirst;
  }

  public void setDsFromFirst(LocalDateTime dsFromFirst) {
    this.dsFromFirst = dsFromFirst;
  }

  public LocalDateTime getDsToLast() {
    return dsToLast;
  }

  public void setDsToLast(LocalDateTime dsToLast) {
    this.dsToLast = dsToLast;
  }

  public BigDecimal getDsWork() {
    return dsWork;
  }

  public void setDsWork(BigDecimal dsWork) {
    this.dsWork = dsWork;
  }

  public BigDecimal getDsWorkRed() {
    return dsWorkRed;
  }

  public void setDsWorkRed(BigDecimal dsWorkRed) {
    this.dsWorkRed = dsWorkRed;
  }

  public BigDecimal getDsWorkPruh() {
    return dsWorkPruh;
  }

  public void setDsWorkPruh(BigDecimal dsWorkPruh) {
    this.dsWorkPruh = dsWorkPruh;
  }

  public BigDecimal getDsObedAut() {
    return dsObedAut;
  }

  public void setDsObedAut(BigDecimal dsObedAut) {
    this.dsObedAut = dsObedAut;
  }

  public Boolean getObedKratky() {
    return obedKratky;
  }

  public void setObedKratky(Boolean obedKratky) {
    this.obedKratky = obedKratky;
  }

  public BigDecimal getDsObedMan() {
    return dsObedMan;
  }

  public void setDsObedMan(BigDecimal dsObedMan) {
    this.dsObedMan = dsObedMan;
  }

  public BigDecimal getDsObed() {
    return dsObed;
  }

  public void setDsObed(BigDecimal dsObed) {
    this.dsObed = dsObed;
  }

  public BigDecimal getDsNem() {
    return dsNem;
  }

  public void setDsNem(BigDecimal dsNem) {
    this.dsNem = dsNem;
  }

  public BigDecimal getDsDov() {
    return dsDov;
  }

  public void setDsDov(BigDecimal dsDov) {
    this.dsDov = dsDov;
  }

  public BigDecimal getDsLek() {
    return dsLek;
  }

  public void setDsLek(BigDecimal dsLek) {
    this.dsLek = dsLek;
  }

  public BigDecimal getDsVol() {
    return dsVol;
  }

  public void setDsVol(BigDecimal dsVol) {
    this.dsVol = dsVol;
  }

  public BigDecimal getDsNa() {
    return dsNa;
  }

  public void setDsNa(BigDecimal dsNa) {
    this.dsNa = dsNa;
  }

  public LocalDate getDsNaDatum() {
    return dsNaDatum;
  }

  public void setDsNaDatum(LocalDate dsNaDatum) {
    this.dsNaDatum = dsNaDatum;
  }

  public BigDecimal getDsVik() {
    return dsVik;
  }

  public void setDsVik(BigDecimal dsVik) {
    this.dsVik = dsVik;
  }

  public Boolean getRs() {
    return rs;
  }

  public void setRs(Boolean rs) {
    this.rs = rs;
  }

  public String getTmp() {
    return tmp;
  }

  public void setTmp(String tmp) {
    this.tmp = tmp;
  }
}
