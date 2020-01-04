package eu.japtor.vizman.backend.entity;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Immutable
//@ReadOnly
@Entity
@Table(name = "ZAK_NAKL_VIEW")
public class Zakn implements Serializable {
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

//    @Transient
    public String getCkzTextRep() {
        StringBuilder builder = new StringBuilder();
        builder .append(getCkont())
                .append(" / ")
                .append(getCzak())
                .append("\n")
                .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

//    @Transient
    public String getKzTextFull() {
        StringBuilder builder = new StringBuilder();
        builder .append(getTextKontNotNull())
                .append(" / ")
                .append(getTextZakNotNull())
        ;
        return builder.toString();
    }

//    @Transient
    public String getCkontNotNull() {
        return null == ckont ? "" : ckont;
    }

//    @Transient
    public String getCzakNotNull() {
        return null == czak ? "" : czak.toString();
    }

//    @Transient
    public String getTextKontNotNull() {
        return null == textKont ? "" : textKont;
    }

//    @Transient
    public String getTextZakNotNull() {
        return null == textZak ? "" : textZak.toString();
    }

//    @Basic
//    @Column(name = "CKONT")
//    private String ckont;
//
//    @Basic
//    @Column(name = "CZAK")
//    private Integer czak;


//    @Basic
//    @Column(name = "SKUPINA")
//    private String skupina;

//    @Basic
//    @Column(name = "ROK")
//    private Integer rok;

//    @Basic
//    @Column(name = "TEXT_KONT")
//    private String textKont;

//    @Basic
//    @Column(name = "TEXT_ZAK")
//    private String textZak;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "MENA")
//    private Mena mena;

    @Column(
            name = "YM_PRUH",
            columnDefinition = "INTEGER"
    )
    @Convert(
            converter = YearMonthIntegerAttributeConverter.class
    )
    private YearMonth ymPruh;

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


    public BigDecimal getKoefP8() {
        return koefP8;
    }

    public BigDecimal getWorkP8() {
        return workP8;
    }

    //    @Transient
    public BigDecimal getNaklMzdaP8() {
        return getNaklMzdaNotNull().multiply(getKoefP8NotNull());
    }

//    @Transient
    public BigDecimal getWorkPruhP8() {
        return getWorkPruhNotNull().multiply(getKoefP8NotNull());
    }

//    @Transient
    public BigDecimal getNaklMzdaPojistP8() {
        return getNaklMzdaPojistNotNull().multiply(getKoefP8NotNull());
    }

//    @Transient
    public BigDecimal getKoefP8NotNull() {
        return null == koefP8 ? BigDecimal.ZERO : koefP8;
    }

//    @Transient
    public BigDecimal getNaklMzdaNotNull() {
        return null == naklMzda ? BigDecimal.ZERO : naklMzda;
    }

//    @Transient
    public BigDecimal getNaklMzdaPojistNotNull() {
        return null == getNaklMzdaPojist() ? BigDecimal.ZERO : getNaklMzdaPojist();
    }


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

    public Long getZakId() {
        return zakId;
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

//    @Transient
    public BigDecimal getWorkPruhNotNull() {
        return null == workPruh ? BigDecimal.ZERO : workPruh;
    }


//    public ItemType getTyp() {
//        return typ;
//    }
//
//    public String getCkont() {
//        return ckont;
//    }
//
//    public Integer getCkz() {
//        return czak;
//    }

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

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    // =============================================

//    @Transient
    public String getPrijmeni() {
        return person == null ? "N/A" : person.getPrijmeni();
    }

//    @Transient
    public BigDecimal getNaklMzdaPojist() {
        if (null == naklMzda || null == naklPojist) {
            return null;
        }
        return naklMzda.add(naklPojist);
    }

//    @Transient
    public BigDecimal getNaklMzdaPojistRezie() {
        return (null == naklMzda ? BigDecimal.ZERO : naklMzda).add(naklPojist).add(naklRezie);
    }

//    @Transient
//    public BigDecimal calcNaklMzdaPojistRezie(BigDecimal koefPojist, BigDecimal koefRezie) {
//        if (null == naklMzda) {
//            return null;
//        } else {
//            return naklMzda.multiply(koefPojist.add(BigDecimal.ONE)).multiply(koefRezie.add(BigDecimal.ONE));
//        }
//    }

//    @Transient
    public BigDecimal calcNaklPojist(BigDecimal koefPojist) {
//        return null == naklMzda ? null : naklMzda.multiply(koefPojist.add(BigDecimal.ONE));
        return null == naklMzda ? null : naklMzda.multiply(koefPojist);
    }

//    @Transient
    public BigDecimal calcNaklRezie(BigDecimal koefRezie) {
//        return null == naklMzda ? null : naklMzda.multiply(koefRezie.add(BigDecimal.ONE));
        return null == naklMzda ? null : naklMzda.multiply(koefRezie);
    }

//    @Transient
//    public String getCkontNotNull() {
//        return null == ckont ? "" : ckont;
//    }
//
//    @Transient
//    public String getCzakNotNull() {
//        return null == czak ? "" : czak.toString();
//    }
//
//    @Transient
//    public String getKzCislo() {
//        StringBuilder builder = new StringBuilder();
//        builder .append(getCkontNotNull())
//                .append(" / ")
//                .append(getCzakNotNull())
//        ;
//        return builder.toString();
//    }

//    @Transient
//    public String getTextKontNotNull() {
//        return null == textKont ? "" : textKont;
//    }
//
//    @Transient
//    public String getTextZakNotNull() {
//        return null == textZak ? "" : textZak.toString();
//    }

//    @Transient
//    private String kzText;
//
//    @Transient
//    public String getKzText() {
//        StringBuilder builder = new StringBuilder();
//        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 25))
//                .append(" / ")
//                .append(getTextZakNotNull())
//        ;
//        return builder.toString();
//    }

//    @Transient
//    private String kzTextShort;
//
//    @Transient
//    public String getKzTextShort() {
//        StringBuilder builder = new StringBuilder();
//        builder .append(StringUtils.substring(getTextKontNotNull(), 0, 15))
//                .append(" / ")
//                .append(StringUtils.substring(getTextZakNotNull(), 0, 15))
//        ;
//        return builder.toString();
//    }


    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AbstractGenIdEntity)) return false;
        return id != null && id.equals(((Zakn) other).id);
    }
}
