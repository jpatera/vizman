package eu.japtor.vizman.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAKT")
public class Fakt extends AbstractEntity implements HasModifDates {

    @Enumerated(EnumType.STRING)
    private ItemType typ;

    private Integer cfakt;
    private BigDecimal plneni;
    private BigDecimal zaklad;
    private BigDecimal castka;
    private String text;
    private LocalDate dateDuzp;
    private LocalDate dateVystav;
    private LocalDateTime dateTimeExport;
    private LocalDate dateCreate;
    private LocalDateTime datetimeUpdate;

    // TODO: add ItemType
//    public Fakt() {
//        this.typ = ItemType.FAKT;
//    }

    @Basic
    @Column(name = "TYP")
    public ItemType getTyp() {
        return typ;
    }

    public void setTyp(ItemType typ) {
        this.typ = typ;
    }

    @Basic
    @Column(name = "CFAKT")
    public Integer getCfakt() {
        return cfakt;
    }

    public void setCfakt(Integer cfakt) {
        this.cfakt = cfakt;
    }

    @Column(name = "PLNENI")
    public BigDecimal getPlneni() {
        return plneni;
    }

    public void setPlneni(BigDecimal plneni) {
        this.plneni = plneni;
    }


    @Column(name = "CASTKA")
    public BigDecimal getCastka() {
        return castka;
    }

    public void setCastka(BigDecimal castka) {
        this.castka = castka;
    }

    @Column(name = "ZAKLAD")
    public BigDecimal getZaklad() {
        return zaklad;
    }

    public void setZaklad(BigDecimal zaklad) {
        this.zaklad = zaklad;
    }


    @Column(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Column(name = "DATE_DUZP")
    public LocalDate getDateDuzp() {
        return dateDuzp;
    }

    public void setDateDuzp(LocalDate dateDuzp) {
        this.dateDuzp = dateDuzp;
    }


    @Column(name = "DATE_VYSTAV")
    public LocalDate getDateVystav() {
        return dateVystav;
    }

    public void setDateVystav(LocalDate dateVystav) {
        this.dateVystav = dateVystav;
    }


    @Column(name = "DATETIME_EXPORT")
    public LocalDateTime getDateTimeExport() {
        return dateTimeExport;
    }

    public void setDateTimeExport(LocalDateTime dateTimeExport) {
        this.dateTimeExport = dateTimeExport;
    }

    public String getDateTimeExportStr() {
        return null == dateTimeExport ? null : dateTimeExport.toString();
    }


    @Column(name = "DATE_CREATE", updatable = false)
    public LocalDate getDateCreate() {
        return dateCreate;
    }

    protected void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }


    @Column(name = "DATETIME_UPDATE", insertable = false, updatable = false)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ZAK")
    private Zak zak;

    public Zak getZak() {
        return zak;
    }

//    public void setZak(Zak zak) {
//        this.zak = zak;
//    }
}
