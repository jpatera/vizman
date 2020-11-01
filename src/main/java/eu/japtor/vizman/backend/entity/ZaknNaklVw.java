package eu.japtor.vizman.backend.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Immutable
//@ReadOnly
@Entity
@Table(name = "ZAK_NAKL_VIEW")
public class ZaknNaklVw implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Basic
    @Column(name = "ZAK_ID")
    private Long zakId;

    @Basic
    @Column(name = "CKONT")
    private String ckont;

    @Basic
    @Column(name = "CZAK")
    private Integer czak;

    @Basic
    @Column(name = "TEXT_KONT")
    private String textKont;

    @Basic
    @Column(name = "TEXT_ZAK")
    private String textZak;

    @Basic
    @Column(name = "DATE_PRUH")
    private LocalDate datePruh;

    @Basic
    @Column(name = "WORK_PRUH")
    private BigDecimal workPruh;

    @Basic
    @Column(name = "KOEF_P8")
    private BigDecimal koefP8;

    @Basic
    @Column(name = "WORK_P8")
    private BigDecimal workP8;

    @Basic
    @Column(name = "NAKL_MZDA")
    private BigDecimal naklMzda;

    @Basic
    @Column(name = "NAKL_POJIST")
    private BigDecimal naklPojist;

    @Basic
    @Column(name = "NAKL_REZIE")
    private BigDecimal naklRezie;

    @Basic
    @Column(name = "SAZBA")
    private BigDecimal sazba;

    @Column(
            name = "YM_PRUH",
            columnDefinition = "INTEGER"
    )
    @Convert(
            converter = YearMonthIntegerAttributeConverter.class
    )
    private YearMonth ymPruh;

    //    @OneToOne(mappedBy = "zakn", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, orphanRemoval = false)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="PERSON_ID", unique=true, nullable=true, insertable=false, updatable=false)
//    @JoinColumn(name = "id", nullable = false)
//    @MapsId
//    @OrderBy("cfakt DESC")
    private Person person;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getZakId() {
        return zakId;
    }
//    public void setZakId(Long zakId) {
//        this.zakId = zakId;
//    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    public String getCkont() {
        return ckont;
    }

    public Integer getCzak() {
        return czak;
    }

    public String getTextKont() {
        return textKont;
    }

    public String getTextZak() {
        return textZak;
    }

    public YearMonth getYmPruh() {
        return ymPruh;
    }
    public void setYmPruh(YearMonth ymPruh) {
        this.ymPruh = ymPruh;
    }

    public LocalDate getDatePruh() {
        return datePruh;
    }

    public BigDecimal getWorkPruh() {
        return workPruh;
    }

    public BigDecimal getNaklMzda() {
        return naklMzda;
    }
    public void setNaklMzda(BigDecimal naklMzda) {
        this.naklMzda = naklMzda;
    }

    public BigDecimal getNaklPojist() {
        return naklPojist;
    }
    public void setNaklPojist(BigDecimal naklPojist) {
        this.naklPojist = naklPojist;
    }

    public BigDecimal getNaklRezie() {
        return naklRezie;
    }
    public void setNaklRezie(BigDecimal naklRezie) {
        this.naklPojist = naklPojist;
    }

    public BigDecimal getSazba() {
        return sazba;
    }


// Transients
// ==========

    public String getKzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

    public String getKzCisloRep() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkont())
                .append("-")
                .append(getCzak())
        ;
        return builder.toString();
    }

    public String getKzCisloTextRep() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkont())
                .append("-")
                .append(getCzak())
                .append(" : ")
//                .append("\n")
                .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

    public String getCkontNotNull() {
        return null == ckont ? "" : ckont;
    }

    public String getCzakNotNull() {
        return null == czak ? "" : czak.toString();
    }

    public String getTextKontNotNull() {
        return null == textKont ? "" : textKont;
    }

    public String getTextZakNotNull() {
        return null == textZak ? "" : textZak.toString();
    }

    public BigDecimal getKoefP8() {
        return koefP8;
    }

    public BigDecimal getWorkP8() {
        return workP8;
    }

    public BigDecimal getNaklMzdaP8() {
        return getNaklMzdaNotNull().multiply(getKoefP8NotNull());
    }

    public BigDecimal getWorkPruhP8() {
        return getWorkPruhNotNull().multiply(getKoefP8NotNull());
    }

    public BigDecimal getNaklMzdaPojistP8() {
        return getNaklMzdaPojistNotNull().multiply(getKoefP8NotNull());
    }

    public BigDecimal getKoefP8NotNull() {
        return null == koefP8 ? BigDecimal.ZERO : koefP8;
    }

    public BigDecimal getNaklMzdaNotNull() {
        return null == naklMzda ? BigDecimal.ZERO : naklMzda;
    }

    public BigDecimal getNaklMzdaPojistNotNull() {
        return null == getNaklMzdaPojist() ? BigDecimal.ZERO : getNaklMzdaPojist();
    }

    public BigDecimal getWorkPruhNotNull() {
        return null == workPruh ? BigDecimal.ZERO : workPruh;
    }

    public String getPrijmeni() {
        return person == null ? "N/A" : person.getPrijmeni();
    }

    public BigDecimal getNaklMzdaPojist() {
        if (null == naklMzda || null == naklPojist) {
            return null;
        }
        return naklMzda.add(naklPojist);
    }

    public BigDecimal getNaklMzdaPojistRezie() {
        return (null == naklMzda ? BigDecimal.ZERO : naklMzda).add(naklPojist).add(naklRezie);
    }

    public BigDecimal calcNaklPojist(BigDecimal koefPojist) {
        return null == naklMzda ? null : naklMzda.multiply(koefPojist);
    }

    public BigDecimal calcNaklRezie(BigDecimal koefRezie) {
//        return null == naklMzda ? null : naklMzda.multiply(koefRezie.add(BigDecimal.ONE));
        return null == naklMzda ? null : naklMzda.multiply(koefRezie);
    }


    // =======================================

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return id != null && id.equals(((ZaknNaklVw) other).id);
    }
}
