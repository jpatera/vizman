package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAKT")
public class Fakt extends AbstractGenIdEntity implements HasModifDates {

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "TYP")
    private ItemType typ;

    @Basic
    @Column(name = "CFAKT")
    private Integer cfakt;

    @Column(name = "DATE_DUZP")
    private LocalDate dateDuzp;

    @Column(name = "PLNENI")
    private BigDecimal plneni;

    @Column(name = "TEXT")
    private String text;

    @Column(name = "CASTKA")
    private BigDecimal castka;

    @Column(name = "ZAKLAD")
    private BigDecimal zaklad;

    @Column(name = "DATE_VYSTAV")
    private LocalDate dateVystav;

    @Column(name = "DATETIME_EXPORT")
    private LocalDateTime dateTimeExport;

    @Column(name = "DATE_CREATE", updatable = false)
    private LocalDate dateCreate;

    @Column(name = "DATETIME_UPDATE", insertable = false, updatable = false)
    private LocalDateTime datetimeUpdate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ZAK")
    private Zak zak;


    // TODO: add ItemType
//    public Fakt() {
//        this.typ = ItemType.FAKT;
//    }

    public ItemType getTyp() {
        return typ;
    }

    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    public Integer getCfakt() {
        return cfakt;
    }

    public void setCfakt(Integer cfakt) {
        this.cfakt = cfakt;
    }

    public BigDecimal getPlneni() {
        return plneni;
    }

    public void setPlneni(BigDecimal plneni) {
        this.plneni = plneni;
    }


    public BigDecimal getCastka() {
        return castka;
    }

    public void setCastka(BigDecimal castka) {
        this.castka = castka;
    }

    public BigDecimal getZaklad() {
        return zaklad;
    }

    public void setZaklad(BigDecimal zaklad) {
        this.zaklad = zaklad;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public LocalDate getDateDuzp() {
        return dateDuzp;
    }

    public void setDateDuzp(LocalDate dateDuzp) {
        this.dateDuzp = dateDuzp;
    }


    public LocalDate getDateVystav() {
        return dateVystav;
    }

    public void setDateVystav(LocalDate dateVystav) {
        this.dateVystav = dateVystav;
    }


    public LocalDateTime getDateTimeExport() {
        return dateTimeExport;
    }

    public void setDateTimeExport(LocalDateTime dateTimeExport) {
        this.dateTimeExport = dateTimeExport;
    }

    public String getDateTimeExportStr() {
        return null == dateTimeExport ? null : dateTimeExport.toString();
    }


    public LocalDate getDateCreate() {
        return dateCreate;
    }

    protected void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }


    public LocalDateTime getDatetimeUpdate() {
        return datetimeUpdate;
    }

    protected void setDatetimeUpdate(LocalDateTime datetimeUpdate) {
        this.datetimeUpdate = datetimeUpdate;
    }

    @Transient
    public Mena getMena() {
        return getZak().getMena();
    }


    public Zak getZak() {
        return zak;
    }

    public void setZak(Zak zak) {
        this.zak = zak;
    }
}
