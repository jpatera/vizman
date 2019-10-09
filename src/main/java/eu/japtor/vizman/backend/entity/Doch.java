package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "DOCH")
//@SequenceGenerator(initialValue = 100001, name = "id_gen", sequenceName = "doch_seq")
public class Doch extends AbstractGenIdEntity {
    public static final String STATE_KONEC = "K";
    public static final String STATE_NONE = null;

    @Basic
    @Column(name = "PERSON_ID")
    private Long personId;

    @Basic
    @Column(name = "USERNAME")
    private String username;

    @Basic
    @Column(name = "DOCH_DATE")
    private LocalDate dochDate;

    @Basic
    @Column(name = "DOCH_STATE")
    private String dochState;

    @Basic
    @Column(name = "FROM_TIME")
    private LocalTime fromTime;

    @Basic
    @Column(name = "FROM_MODIF_DATETIME")
    private LocalDateTime fromModifDatetime;

    @Basic
    @Column(name = "FROM_MANUAL")
    private Boolean fromManual;

    @Basic
    @Column(name = "TO_TIME")
    private LocalTime toTime;

    @Basic
    @Column(name = "TO_MODIF_DATETIME")
    private LocalDateTime toModifDatetime;

//    @Basic
//    @Column(name = "DOCH_DURATION")
//    LocalTime dochDuration;

    @Basic
    @Column(name = "DOCH_DUR")
    Duration dochDur;

    @Basic
    @Column(name = "TO_MANUAL")
    private Boolean toManual;

    @Basic
    @Column(name = "cin_id")
    private Long cinId;

    @Basic
    @Column(name = "CDOCH")
    private Integer cdoch;

    @Basic
    @Column(name = "CIN_AKCE_TYP")
    private String cinAkceTyp;

    @Basic
    @Column(name = "CIN_CIN_KOD")
    @Enumerated(EnumType.STRING)
    private Cin.CinKod cinCinKod;

    @Basic
    @Column(name = "CINNOST")
    private String cinnost;

    @Basic
    @Column(name = "CALCPRAC")
    private Boolean calcprac;

    @Basic
    @Column(name = "POZNAMKA")
    private String poznamka;

    @Basic
    @Column(name = "TMP")
    private String tmp;

//    @Transient
//    private Duration dochDurationUI;


    public Doch() {}

    public Doch(
            final LocalDate dochDate
            , final Person person
            , final Cin cin
            , final LocalTime fromTime
            , final LocalDateTime fromModifStamp
            , boolean fromManual
            , String poznamka
    ) {
        this.personId = person.getId();
        this.username = person.getUsername();
        this.dochDate = dochDate;
        this.cinId = cin.getId();
        this.cinAkceTyp = cin.getAkceTyp();
        this.cinCinKod = cin.getCinKod();
        this.cinnost = cin.getCinnost();
        this.calcprac = cin.getCalcprac();
        if (!Cin.ATYP_FIX_CAS.equals(cin.getAkceTyp())) {
            this.fromTime = fromTime;
            this.fromModifDatetime = fromModifStamp;
        }
        this.fromManual = fromManual;
        this.poznamka = poznamka;
    }

    public static Doch createSingleFixed(
            final LocalDate dochDate
            , final Person person
            , final String state
            , final Cin cin
            , final Duration duration
            , String poznamka
    ) {
        Doch singleDoch = new Doch();
        singleDoch.personId = person.getId();
        singleDoch.username = person.getUsername();
        singleDoch.dochDate = dochDate;
        singleDoch.dochState = state;
        singleDoch.cinId = cin.getId();
        singleDoch.cinAkceTyp = cin.getAkceTyp();
        singleDoch.cinCinKod = cin.getCinKod();
        singleDoch.cinnost = cin.getCinnost();
        singleDoch.calcprac = cin.getCalcprac();
        singleDoch.poznamka = poznamka;
        singleDoch.dochDur = duration;

        return singleDoch;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getdDochDate() {
        return dochDate;
    }

    public void setdDochDate(LocalDate dochDate) {
        this.dochDate = dochDate;
    }

    public LocalDateTime getFromModifDatetime() {
        return fromModifDatetime;
    }

    public void setFromModifDatetime(LocalDateTime fromModifDatetime) {
        this.fromModifDatetime = fromModifDatetime;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public Boolean getFromManual() {
        return fromManual;
    }

    public void setFromManual(Boolean fromManual) {
        this.fromManual = fromManual;
    }

    public LocalDateTime getToModifDatetime() {
        return toModifDatetime;
    }

    public void setToModifDatetime(LocalDateTime toModifDatetime) {
        this.toModifDatetime = toModifDatetime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public Boolean getToManual() {
        return toManual;
    }

    public void setToManual(Boolean toManual) {
        this.toManual = toManual;
    }

    public Duration getDochDur() {
        return dochDur;
    }

    public void setDochDur(Duration dochDur) {
        this.dochDur = dochDur;
    }

//    @Basic
//    @Column(name = "DOCH_DURATION")
//    public LocalTime getDochDuration() {
//        return dochDuration;
//    }
//
//    public void setDochDurationFromUI(Duration dochDuration) {
//        this.dochDuration = dochDuration;
//    }

//    @Transient
//    public Duration getDochDurationUI() {
//        return this.dochDurationUI;
//    }

    @Transient
    public boolean hasClosedState() {
        return null != dochState && dochState.equals(STATE_KONEC);
    }

    @Transient
    public Duration getSignedDochDur() {
        if (null == dochDur) {
            return Duration.ZERO;
        } else if (Cin.CinKod.OA == cinCinKod) {
            return Duration.ZERO.minus(dochDur);
        } else {
            return dochDur;
        }
    }

//    @Transient
//    public void setDochDurationFromUI(Duration dochDurationUI) {
//        this.dochDuration = dochDurationUI == null ? null : LocalTime.MIDNIGHT.plus(dochDurationUI);
//    }



    @PostLoad
    public void init() {
//        this.dochDurationUI = this.dochDuration == null ? null : Duration.between(LocalTime.MIDNIGHT, dochDuration);
//        this.dochDur = this.dochDuration == null ? null : Duration.between(LocalTime.MIDNIGHT, dochDuration);
//        this.dochDuration = this.dbDochDuration == null ? null : LocalTime.MIDNIGHT.plus(this.dbDochDuration);
//        this.myDuration = this.myDurationString == null ? null : Duration.parse(this.myDurationString);
    };


    public Long getCinId() {
        return cinId;
    }

    public void setCinId(Long cinId) {
        this.cinId = cinId;
    }

    public Integer getCdoch() {
        return cdoch;
    }

    public void setCdoch(Integer cdoch) {
        this.cdoch = cdoch;
    }

//    @Basic
//    @Column(name = "CIN_ST")
//    public String getCinSt() {
//        return cinSt;
//    }
//
//    public void setCinSt(String cinSt) {
//        this.cinSt = cinSt;
//    }

    public String getDochState() {
        return dochState;
    }

    public void setDochState(String dochState) {
        this.dochState = dochState;
    }

    public String getCinAkceTyp() {
        return cinAkceTyp;
    }

    public void setCinAkceTyp(String cinAkceTyp) {
        this.cinAkceTyp = cinAkceTyp;
    }

    public Cin.CinKod getCinCinKod() {
        return cinCinKod;
    }

    public void setCinCinKod(Cin.CinKod cinCinKod) {
        this.cinCinKod = cinCinKod;
    }

    public String getCinnost() {
        return cinnost;
    }

    public String getCinnostForCell() {
        if (StringUtils.isBlank(poznamka)) {
            return cinnost;
        } else {
            return cinnost + "\r\n" + "[" + poznamka + "]";
        }
    }

    public void setCinnost(String cinnost) {
        this.cinnost = cinnost;
    }

    public Boolean getCalcprac() {
        return calcprac;
    }

    public void setCalcprac(Boolean calcprac) {
        this.calcprac = calcprac;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }


    @Transient
    public boolean isClosed() {
        return (Cin.ATYP_KONEC_CIN).equals(dochState);
    }

    @Transient
    public boolean isNemoc() {
        return (cinCinKod.equals(Cin.CinKod.ne));
    }

    @Transient
    public boolean isSluzebka() {
        return (cinCinKod.equals(Cin.CinKod.PM) && Cin.ATYP_FIX_CAS.equals(cinAkceTyp));
    }

    @Transient
    public boolean isDovolenaFull() {
        return (cinCinKod.equals(Cin.CinKod.dc));
    }

    @Transient
    public boolean isDovolenaHalf() {
        return (cinCinKod.equals(Cin.CinKod.dp));
    }

    @Transient
    public boolean isNahradniVolno() {
        return cinCinKod.equals(Cin.CinKod.nv);
    }

    @Transient
    public boolean isZk() {
        return cinAkceTyp.equals(Cin.ATYP_ZACATEK_KONEC_CIN);
    }

//    @Basic
//    @Column(name = "CZAK")
//    public String getCkz() {
//        return czak;
//    }
//
//    public void setCkz(String czak) {
//        this.czak = czak;
//    }
//
//    @Basic
//    @Column(name = "CZAK")
//    public Integer getCkz() {
//        return czak;
//    }
//
//    public void setCkz(Integer czak) {
//        this.czak = czak;
//    }
//
//    @Basic
//    @Column(name = "TYP_DOKLADU")
//    public String getTypDokladu() {
//        return typDokladu;
//    }
//
//    public void setTypDokladu(String typDokladu) {
//        this.typDokladu = typDokladu;
//    }
//
//    @Basic
//    @Column(name = "ROKZAK")
//    public Short getRokzak() {
//        return rokzak;
//    }
//
//    public void setRokzak(Short rokzak) {
//        this.rokzak = rokzak;
//    }
//
//    @Basic
//    @Column(name = "ROKMESZAD")
//    public String getRokmeszad() {
//        return rokmeszad;
//    }
//
//    public void setRokmeszad(String rokmeszad) {
//        this.rokmeszad = rokmeszad;
//    }
//
//    @Basic
//    @Column(name = "TEXT")
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    @Basic
//    @Column(name = "X")
//    public Boolean getX() {
//        return x;
//    }
//
//    public void setX(Boolean x) {
//        this.x = x;
//    }
//
//    @Basic
//    @Column(name = "HONORAR")
//    public BigDecimal getHonorar() {
//        return honorar;
//    }
//
//    public void setHonorar(BigDecimal honorar) {
//        this.honorar = honorar;
//    }
//
//    @Basic
//    @Column(name = "ROZPRAC")
//    public BigDecimal getRozprac() {
//        return rozprac;
//    }
//
//    public void setRozprac(BigDecimal rozprac) {
//        this.rozprac = rozprac;
//    }
//
//    @Basic
//    @Column(name = "TMP")
//    public String getTmp() {
//        return tmp;
//    }
//
//    public void setTmp(String tmp) {
//        this.tmp = tmp;
//    }
//
//    @Basic
//    @Column(name = "ARCH")
//    public Boolean getArch() {
//        return arch;
//    }
//
//    public void setArch(Boolean arch) {
//        this.arch = arch;
//    }
//
//    @Basic
//    @Column(name = "R_ZAL")
//    public Integer getrZal() {
//        return rZal;
//    }
//
//    public void setrZal(Integer rZal) {
//        this.rZal = rZal;
//    }
//
//    @Basic
//    @Column(name = "R1")
//    public BigDecimal getR1() {
//        return r1;
//    }
//
//    public void setR1(BigDecimal r1) {
//        this.r1 = r1;
//    }
//
//    @Basic
//    @Column(name = "R2")
//    public BigDecimal getR2() {
//        return r2;
//    }
//
//    public void setR2(BigDecimal r2) {
//        this.r2 = r2;
//    }
//
//    @Basic
//    @Column(name = "R3")
//    public BigDecimal getR3() {
//        return r3;
//    }
//
//    public void setR3(BigDecimal r3) {
//        this.r3 = r3;
//    }
//
//    @Basic
//    @Column(name = "R4")
//    public BigDecimal getR4() {
//        return r4;
//    }
//
//    public void setR4(BigDecimal r4) {
//        this.r4 = r4;
//    }
//
//    @Basic
//    @Column(name = "SKUPINA")
//    public String getSkupina() {
//        return skupina;
//    }
//
//    public void setSkupina(String skupina) {
//        this.skupina = skupina;
//    }
//
//    @Basic
//    @Column(name = "RM")
//    public BigDecimal getRm() {
//        return rm;
//    }
//
//    public void setRm(BigDecimal rm) {
//        this.rm = rm;
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Zak zakentity = (Zak) o;
//
//        if (id != zakentity.id) return false;
//        if (cisloZakazky != null ? !cisloZakazky.equals(zakentity.cisloZakazky) : zakentity.cisloZakazky != null)
//            return false;
//        if (idZakazky != null ? !idZakazky.equals(zakentity.idZakazky) : zakentity.idZakazky != null) return false;
//        if (typDokladu != null ? !typDokladu.equals(zakentity.typDokladu) : zakentity.typDokladu != null) return false;
//        if (rokzak != null ? !rokzak.equals(zakentity.rokzak) : zakentity.rokzak != null) return false;
//        if (rokmeszad != null ? !rokmeszad.equals(zakentity.rokmeszad) : zakentity.rokmeszad != null) return false;
//        if (text != null ? !text.equals(zakentity.text) : zakentity.text != null) return false;
//        if (x != null ? !x.equals(zakentity.x) : zakentity.x != null) return false;
//        if (honorar != null ? !honorar.equals(zakentity.honorar) : zakentity.honorar != null) return false;
//        if (rozprac != null ? !rozprac.equals(zakentity.rozprac) : zakentity.rozprac != null) return false;
//        if (tmp != null ? !tmp.equals(zakentity.tmp) : zakentity.tmp != null) return false;
//        if (arch != null ? !arch.equals(zakentity.arch) : zakentity.arch != null) return false;
//        if (rZal != null ? !rZal.equals(zakentity.rZal) : zakentity.rZal != null) return false;
//        if (r1 != null ? !r1.equals(zakentity.r1) : zakentity.r1 != null) return false;
//        if (r2 != null ? !r2.equals(zakentity.r2) : zakentity.r2 != null) return false;
//        if (r3 != null ? !r3.equals(zakentity.r3) : zakentity.r3 != null) return false;
//        if (r4 != null ? !r4.equals(zakentity.r4) : zakentity.r4 != null) return false;
//        if (skupina != null ? !skupina.equals(zakentity.skupina) : zakentity.skupina != null) return false;
//        if (rm != null ? !rm.equals(zakentity.rm) : zakentity.rm != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = (int) (id ^ (id >>> 32));
//        result = 31 * result + (cisloZakazky != null ? cisloZakazky.hashCode() : 0);
//        result = 31 * result + (idZakazky != null ? idZakazky.hashCode() : 0);
//        result = 31 * result + (typDokladu != null ? typDokladu.hashCode() : 0);
//        result = 31 * result + (rokzak != null ? rokzak.hashCode() : 0);
//        result = 31 * result + (rokmeszad != null ? rokmeszad.hashCode() : 0);
//        result = 31 * result + (text != null ? text.hashCode() : 0);
//        result = 31 * result + (x != null ? x.hashCode() : 0);
//        result = 31 * result + (honorar != null ? honorar.hashCode() : 0);
//        result = 31 * result + (rozprac != null ? rozprac.hashCode() : 0);
//        result = 31 * result + (tmp != null ? tmp.hashCode() : 0);
//        result = 31 * result + (arch != null ? arch.hashCode() : 0);
//        result = 31 * result + (rZal != null ? rZal.hashCode() : 0);
//        result = 31 * result + (r1 != null ? r1.hashCode() : 0);
//        result = 31 * result + (r2 != null ? r2.hashCode() : 0);
//        result = 31 * result + (r3 != null ? r3.hashCode() : 0);
//        result = 31 * result + (r4 != null ? r4.hashCode() : 0);
//        result = 31 * result + (skupina != null ? skupina.hashCode() : 0);
//        result = 31 * result + (rm != null ? rm.hashCode() : 0);
//        return result;
//    }

}
