package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.YearMonth;

@Immutable
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

    @Column(name = "DOCH_YM", columnDefinition = "INTEGER")
    @Convert(converter = YearMonthIntegerAttributeConverter.class)
    private YearMonth dochYm;

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

    @Transient
    private String yearFondHours;

    @Transient
    private String yearFondDays;

    // Constructors
    // ------------
    public DochYear() {}


    // Getters & setters
    // -----------------
    public Long getId() {
        return id;
    }

    public Long getPersonId() {
        return personId;
    }

    public YearMonth getDochYm() {
        return dochYm;
    }
    public Integer getDochYear() {
        return dochYm.getYear();
    }

    public Long getPracDobaMins() {
        return null;
    }

    public Duration getDurPracCelk() {
        return durPracCelk;
    }
    public Long getPracCelkMins() {
        return null == durPracCelk ? null : durPracCelk.toMinutes();
    }

    public Duration getDurPracWend() {
        return durPracWend;
    }
    public Long getPracWendMins() {
        return null == durPracWend ? null : durPracWend.toMinutes();
    }

    public Duration getDurLek() {
        return durLek;
    }
    public Long getLekMins() {
        return null == durLek ? null : durLek.toMinutes();
    }

    public Duration getDurDov() {
        return durDov;
    }
    public Long getDovMins() {
        return null == durDov ? null : durDov.toMinutes();
    }

    public Duration getDurNem() {
        return durNem;
    }
    public Long getNemMins() {
        return null == durNem ? null : durNem.toMinutes();
    }

    public Duration getDurVolno() {
        return durVolno;
    }
    public Long getVolnoMins() {
        return null == durVolno ? null : durVolno.toMinutes();
    }

    public Duration getDurSluz() {
        return durSluz;
    }
    public Long getSluzMins() {
        return null == durSluz ? null : durSluz.toMinutes();
    }

    public Person getPerson() {
        return person;
    }

    public String getYearFondHours() {
        return yearFondHours;
    }
    public void setYearFondHours(String yearFondHours) {
        this.yearFondHours = yearFondHours;
    }

    public String getYearFondDays() {
        return yearFondDays;
    }
    public void setYearFondDays(String yearFondDays) {
        this.yearFondDays = yearFondDays;
    }
    public String getFullNameAndDochYear() {
        return person.getPrijmeni() + " " + person.getJmeno()
                + " \u00A0\u00A0\u00A0\u00A0 " + getDochYear()
                + " \u00A0\u00A0\u00A0\u00A0 Fond: " + getYearFondDays() + " [dny]"
                + " \u00A0\u00A0\u00A0\u00A0 Fond: " + getYearFondHours() + " [hod]";
    }

    public String getFullName() {
        return person.getPrijmeni() + " " + person.getJmeno();
    }
}
