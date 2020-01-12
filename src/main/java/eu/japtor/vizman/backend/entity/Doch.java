package eu.japtor.vizman.backend.entity;

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
    @Column(name = "DOCH_DATE")
    private LocalDate dochDate;

    @Basic
    @Column(name = "DOCH_STATE")
    private String dochState;

    @Basic
    @Column(name = "FROM_TIME")
    private LocalTime fromTime;

    @Basic
    @Column(name = "FROM_MANUAL")
    private Boolean fromManual;

    @Basic
    @Column(name = "TO_TIME")
    private LocalTime toTime;

    @Basic
    @Column(name = "DOCH_DUR")
    private Duration dochDur;

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


    public Doch() {}

    public Doch(
            final LocalDate dochDate
            , final Person person
            , final Cin cin
            , final LocalTime fromTime
            , boolean fromManual
            , String poznamka
    ) {
        this.personId = person.getId();
        this.dochDate = dochDate;
        this.cinId = cin.getId();
        this.cinAkceTyp = cin.getAkceTyp();
        this.cinCinKod = cin.getCinKod();
        this.cinnost = cin.getCinnost();
        this.calcprac = cin.getCalcprac();
        if (!Cin.ATYP_FIX_CAS.equals(cin.getAkceTyp())) {
            this.fromTime = fromTime;
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

//    @PostLoad
//    public void init() {
//        this.dochDurationUI = this.dochDuration == null ? null : Duration.between(LocalTime.MIDNIGHT, dochDuration);
//    };

    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public LocalDate getDochDate() {
        return dochDate;
    }
    public void setDochDate(LocalDate dochDate) {
        this.dochDate = dochDate;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public Boolean getFromManual() {
        return fromManual;
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

    public void setCdoch(Integer cdoch) {
        this.cdoch = cdoch;
    }

    public String getDochState() {
        return dochState;
    }
    public void setDochState(String dochState) {
        this.dochState = dochState;
    }

    public String getCinAkceTyp() {
        return cinAkceTyp;
    }

    public Cin.CinKod getCinCinKod() {
        return cinCinKod;
    }

    public String getCinnost() {
        return cinnost;
    }

    public Boolean getCalcprac() {
        return calcprac;
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


    // Custom methods
    // --------------

//    public String getCinnostForCell() {
//        if (StringUtils.isBlank(poznamka)) {
//            return cinnost;
//        } else {
//            return cinnost + "\r\n" + "[" + poznamka + "]";
//        }
//    }

    public boolean hasClosedState() {
        return null != dochState && dochState.equals(STATE_KONEC);
    }

    public boolean isClosed() {
        return (Cin.ATYP_KONEC_CIN).equals(dochState);
    }

    public boolean isNemoc() {
        return (cinCinKod.equals(Cin.CinKod.ne));
    }

    public boolean isSluzebka() {
        return (cinCinKod.equals(Cin.CinKod.PM) && Cin.ATYP_FIX_CAS.equals(cinAkceTyp));
    }

    public boolean isDovolenaFull() {
        return (cinCinKod.equals(Cin.CinKod.dc));
    }

    public boolean isDovolenaHalf() {
        return (cinCinKod.equals(Cin.CinKod.dp));
    }

    public boolean isNahradniVolno() {
        return cinCinKod.equals(Cin.CinKod.nv);
    }

    public boolean isZk() {
        return cinAkceTyp.equals(Cin.ATYP_ZACATEK_KONEC_CIN);
    }
}
