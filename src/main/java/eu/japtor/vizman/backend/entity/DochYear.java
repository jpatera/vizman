package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.YearMonth;

@Immutable
//@ReadOnly
@Entity
@Table(name = "DOCH_ROK_VIEW")
public class DochYear implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Basic
    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(
            name = "DOCH_YM",
            columnDefinition = "INTEGER"
    )
    @Convert(
            converter = YearMonthIntegerAttributeConverter.class
    )
    private YearMonth dochYm;

//    @Basic
//    @Column(name = "DOCH_WEEK")
//    private Integer dochWeek;
//
//    @Basic
//    @Column(name = "DOCH_DATE")
//    private LocalDate dochDate;

//    @Basic
//    @Column(name = "DOCH_STATE")
//    private String dochState;

//    @Basic
//    @Column(name = "FROM_PRACE_START")
//    private LocalTime fromPraceStart;
//
//    @Basic
//    @Column(name = "FROM_MANUAL")
//    private Boolean fromManual;
//
//    @Basic
//    @Column(name = "TO_PRACE_END")
//    private LocalTime toPraceEnd;
//
//    @Basic
//    @Column(name = "TO_MANUAL")
//    private Boolean toManual;

//    @Basic
//    @Column(name = "DUR_OBED")
//    private Duration durObed;
//
//    @Basic
//    @Column(name = "OBED_AUTO")
//    private Boolean obedAuto;

    @Basic
    @Column(name = "DUR_PRACE_CELK")
    private Duration durPracCelk;

    @Basic
    @Column(name = "DUR_PRACE_WEND")
    private Duration durPracWend;

    @Basic
    @Column(name = "DUR_LEK")
    private Duration durLek;

    @Basic
    @Column(name = "DUR_DOV")
    private Duration durDov;

    @Basic
    @Column(name = "DUR_NEM")
    private Duration durNem;

    @Basic
    @Column(name = "DUR_VOLNO")
    private Duration durVolno;

    @Basic
    @Column(name = "DUR_SLUZ")
    private Duration durSluz;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="PERSON_ID", unique=true, nullable=true, insertable=false, updatable=false)
    private Person person;

    public Person getPerson() {
        return person;
    }


    @Transient
    private String yearFondHours;

    public String getYearFondHours() {
        return yearFondHours;
    }
    public void setYearFondHours(String yearFondHours) {
        this.yearFondHours = yearFondHours;
    }

    @Transient
    private String yearFondDays;

    public String getYearFondDays() {
        return yearFondDays;
    }
    public void setYearFondDays(String yearFondDays) {
        this.yearFondDays = yearFondDays;
    }

    // Constructor
    // -----------
    public DochYear() {}


    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

//    public Integer getDochWeek() {
//        return dochWeek;
//    }
//
//    public LocalDate getDochDate() {
//        return dochDate;
//    }

    public YearMonth getDochYm() {
        return dochYm;
    }
    @Transient
    public Integer getDochYear() {
        return dochYm.getYear();
    }

    public void setDochYm(YearMonth dochYm) {
        this.dochYm = dochYm;
    }

//    public LocalTime getFromPraceStart() {
//        return fromPraceStart;
//    }
//
//    public Boolean getFromManual() {
//        return fromManual;
//    }
//
//    public LocalTime getToPraceEnd() {
//        return toPraceEnd;
//    }
//
//    public Boolean getToManual() {
//        return toManual;
//    }

//    public Duration getDurObed() {
//        return durObed;
//    }
//    @Transient
//    public Long getObedMins() {
//        return null == durObed ? null : durObed.toMinutes();
//    }
//
//    public Boolean getObedAuto() {
//        return obedAuto;
//    }

    public Duration getDurPracCelk() {
        return durPracCelk;
    }
    @Transient
    public Long getPracCelkMins() {
        return null == durPracCelk ? null : durPracCelk.toMinutes();
    }

    public Duration getDurPracWend() {
        return durPracWend;
    }
    @Transient
    public Long getPracWendMins() {
        return null == durPracWend ? null : durPracWend.toMinutes();
    }

    public Duration getDurLek() {
        return durLek;
    }
    @Transient
    public Long getLekMins() {
        return null == durLek ? null : durLek.toMinutes();
    }

    public Duration getDurDov() {
        return durDov;
    }
    @Transient
    public Long getDovMins() {
        return null == durDov ? null : durDov.toMinutes();
    }

    public Duration getDurNem() {
        return durNem;
    }
    @Transient
    public Long getNemMins() {
        return null == durNem ? null : durNem.toMinutes();
    }

    public Duration getDurVolno() {
        return durVolno;
    }
    @Transient
    public Long getVolnoMins() {
        return null == durVolno ? null : durVolno.toMinutes();
    }

    public Duration getDurSluz() {
        return durSluz;
    }
    @Transient
    public Long getSluzMins() {
        return null == durSluz ? null : durSluz.toMinutes();
    }


    @Transient
    public String getFullNameAndDochYear() {
        return person.getPrijmeni() + " " + person.getJmeno()
                + " \u00A0\u00A0\u00A0\u00A0 " + getDochYear()
                + " \u00A0\u00A0\u00A0\u00A0 Fond: " + getYearFondDays() + " [dny]"
                + " \u00A0\u00A0\u00A0\u00A0 Fond: " + getYearFondHours() + " [hod]";
    }

    @Transient
    public String getFullName() {
        return person.getPrijmeni() + " " + person.getJmeno();
    }


//    @Transient
//    public String getCompositeDate() {
//        return dochDate.format(DateTimeFormatter.ofPattern("d.M. E"));
//    }
//
//    @Transient
//    public YearMonth getDochYm() {
//        return YearMonth.from(dochDate);
//    }

    @Transient
    public Long getPracDobaMins() {
        return null;

//        if (null == fromPraceStart || null == toPraceEnd || dochDate.getDayOfWeek().getValue() >= 6) {
//            return null;
//        }
//        Duration durBeforeStart = pracDobaStart.isAfter(fromPraceStart) ?
//                Duration.between(fromPraceStart, pracDobaStart) : Duration.ZERO;
//        Duration durAfterEnd = pracDobaEnd.isBefore(toPraceEnd) ?
//                Duration.between(pracDobaEnd, toPraceEnd) : Duration.ZERO;
//        Duration deadTime = durPracCelk.minus(durBeforeStart).minus(durAfterEnd);
//        return deadTime.compareTo(Duration.ZERO) > 0 ? deadTime.toMinutes() : Duration.ZERO.toMinutes();
    }
}
