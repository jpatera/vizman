package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Immutable
//@ReadOnly
@Entity
@Table(name = "DOCH_MES_VIEW")
public class DochMes implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final LocalTime pracDobaStart = LocalTime.of(8, 0, 0);
    private static final LocalTime pracDobaEnd = LocalTime.of(18, 0, 0);

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Basic
    @Column(name = "PERSON_ID")
    private Long personId;

    @Basic
    @Column(name = "DOCH_WEEK")
    private Integer dochWeek;

    @Basic
    @Column(name = "DOCH_DATE")
    private LocalDate dochDate;

//    @Basic
//    @Column(name = "DOCH_STATE")
//    private String dochState;

    @Basic
    @Column(name = "FROM_PRACE_START")
    private LocalTime fromPraceStart;

    @Basic
    @Column(name = "FROM_MANUAL")
    private Boolean fromManual;

    @Basic
    @Column(name = "TO_PRACE_END")
    private LocalTime toPraceEnd;

    @Basic
    @Column(name = "TO_MANUAL")
    private Boolean toManual;

    @Basic
    @Column(name = "DUR_OBED")
    private Duration durObed;

    @Basic
    @Column(name = "OBED_AUTO")
    private Boolean obedAuto;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="PERSON_ID", unique=true, nullable=true, insertable=false, updatable=false)
    private Person person;

    public Person getPerson() {
        return person;
    }

    // Constructor
    // -----------
    public DochMes() {}


    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

    public Integer getDochWeek() {
        return dochWeek;
    }

    public LocalDate getDochDate() {
        return dochDate;
    }

    public LocalTime getFromPraceStart() {
        return fromPraceStart;
    }

    public Boolean getFromManual() {
        return fromManual;
    }

    public LocalTime getToPraceEnd() {
        return toPraceEnd;
    }

    public Boolean getToManual() {
        return toManual;
    }

    public Duration getDurObed() {
        return durObed;
    }
    @Transient
    public Long getObedMins() {
        return durObed.toMinutes();
    }

    public Boolean getObedAuto() {
        return obedAuto;
    }

    public Duration getDurPracCelk() {
        return durPracCelk;
    }
    @Transient
    public Long getPracCelkMins() {
        return durPracCelk.toMinutes();
    }

    public Duration getDurPracWend() {
        return durPracWend;
    }
    @Transient
    public Long getPracWendMins() {
        return durPracWend.toMinutes();
    }

    public Duration getDurLek() {
        return durLek;
    }
    @Transient
    public Long getLekMins() {
        return durLek.toMinutes();
    }

    public Duration getDurDov() {
        return durDov;
    }
    @Transient
    public Long getDovMins() {
        return durDov.toMinutes();
    }

    public Duration getDurNem() {
        return durNem;
    }
    @Transient
    public Long getNemMins() {
        return durNem.toMinutes();
    }

    public Duration getDurVolno() {
        return durVolno;
    }
    @Transient
    public Long getVolnoMins() {
        return durVolno.toMinutes();
    }


    @Transient
    public String getFullNameAndDochYm() {
        return person.getPrijmeni() + " " + person.getJmeno() + ", " + getDochYm();
    }


    @Transient
    public String getCompositeDate() {
        return dochDate.format(DateTimeFormatter.ofPattern("d.M. E"));
    }

    @Transient
    public YearMonth getDochYm() {
        return YearMonth.from(dochDate);
    }

    @Transient
    public Long getPracDobaMins() {
        if (null == fromPraceStart || null == toPraceEnd || dochDate.getDayOfWeek().getValue() >= 6) {
            return null;
        }
        Duration durBeforeStart = pracDobaStart.isAfter(fromPraceStart) ?
                Duration.between(fromPraceStart, pracDobaStart) : Duration.ZERO;
        Duration durAfterEnd = pracDobaEnd.isBefore(toPraceEnd) ?
                Duration.between(pracDobaEnd, toPraceEnd) : Duration.ZERO;
        Duration deadTime = durPracCelk.minus(durBeforeStart).minus(durAfterEnd);
        return deadTime.compareTo(Duration.ZERO) > 0 ? deadTime.toMinutes() : Duration.ZERO.toMinutes();
    }
}
